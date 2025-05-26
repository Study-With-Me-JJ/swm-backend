package com.jj.swm.domain.study.comment.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.request.UpsertStudyCommentRequest;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.fixture.dto.request.UpsertStudyCommentRequestFixture;
import com.jj.swm.domain.study.comment.repository.StudyCommentRepository;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class StudyCommentCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    private static final int THREAD_COUNT = 100;

    // target service
    @Autowired
    private StudyCommentCommandService commentCommandService;

    // service
    @Autowired
    private StudyCommandService studyCommandService;

    // repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyCommentRepository commentRepository;

    // entity
    private User user;
    private Long commentId;
    private final Long studyId = 1L; // setUp에서 생성한 study id

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        commentId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                studyId,
                null,
                user.getId()
        ).getCommentId();
    }

    @Test
    @DisplayName("스터디 모집 댓글 생성에 성공한다.")
    void createComment_Success() {
        //given
        UpsertStudyCommentRequest createRequest = UpsertStudyCommentRequestFixture.create();

        //when
        Long newCommentId = commentCommandService.createComment(
                createRequest,
                studyId,
                null,
                user.getId()
        ).getCommentId();

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(2, study.getStatistics().getCommentCount());

        Optional<StudyComment> optionalComment = commentRepository.findById(newCommentId);
        assertTrue(optionalComment.isPresent());

        StudyComment comment = optionalComment.get();
        assertEquals(createRequest.getContent(), comment.getContent());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 생성에 성공한다.")
    void createComment_WhenReply_Success() {
        //when
        Long replyId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                studyId,
                commentId,
                user.getId()
        ).getCommentId();

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(1, study.getStatistics().getCommentCount());

        StudyComment reply = commentRepository.findById(replyId).get();
        assertEquals(commentId, reply.getParent().getId());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 id에 대해 대댓글을 생성해도 성공한다.")
    void createComment_WhenReplyWithReplyId_Success() {
        //given
        UpsertStudyCommentRequest createRequest = UpsertStudyCommentRequestFixture.create();
        Long replyId = commentCommandService.createComment(
                createRequest,
                studyId,
                commentId,
                user.getId()
        ).getCommentId();

        //when
        Long reRePlyId = commentCommandService.createComment(
                createRequest,
                studyId,
                replyId,
                user.getId()
        ).getCommentId();

        //then
        StudyComment reReply = commentRepository.findById(reRePlyId).get();
        assertEquals(commentId, reReply.getParent().getId());
    }

    @Test
    @DisplayName("댓글 생성 시 스터디 모집 댓글 수 동시성 제어에 성공한다.")
    void createComment_Concurrency_Success() throws InterruptedException {
        //given
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    commentCommandService.createComment(
                            UpsertStudyCommentRequestFixture.create(),
                            studyId,
                            null,
                            user.getId()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(THREAD_COUNT + 1, study.getStatistics().getCommentCount()); // setUp에서 기본 생성에 의해 +1 설정
    }

    @Test
    @DisplayName("스터디 모집 댓글 수정에 성공한다.")
    void updateComment_Success() {
        //given
        UpsertStudyCommentRequest updateRequest = UpsertStudyCommentRequestFixture.update();

        //when
        commentCommandService.updateComment(
                updateRequest,
                commentId,
                user.getId()
        );

        //then
        StudyComment comment = commentRepository.findById(commentId).get();

        assertEquals(updateRequest.getContent(), comment.getContent());
        assertNotEquals(comment.getCreatedAt(), comment.getUpdatedAt());
    }

    @Test
    @DisplayName("스터디 모집 댓글 삭제에 성공한다.")
    void deleteComment_Success() {
        //given
        commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                studyId,
                commentId,
                user.getId()
        );

        //when
        commentCommandService.deleteComment(commentId, user.getId());

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(0, study.getStatistics().getCommentCount());

        assertEquals(0, commentRepository.count());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 삭제에 성공한다.")
    void deleteComment_WhenReply_Success() {
        //given
        Long replyId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                studyId,
                commentId,
                user.getId()
        ).getCommentId();

        //when
        commentCommandService.deleteComment(replyId, user.getId());

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(1, study.getStatistics().getCommentCount());

        Optional<StudyComment> optionalComment = commentRepository.findById(replyId);
        assertFalse(optionalComment.isPresent());
    }

    @Test
    @DisplayName("댓글 삭제 시 스터디 모집 댓글 수 동시성 제어에 성공한다.")
    void deleteStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        List<Long> commentIds = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            commentIds.add(commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    studyId,
                    null,
                    user.getId()
            ).getCommentId());
        }

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            Long commentId = commentIds.get(i);
            executorService.submit(() -> {
                try {
                    commentCommandService.deleteComment(commentId, user.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        //then
        assertEquals(1, commentRepository.count()); // setUp에서 기본 생성에 의해 1 설정

        Study study = studyRepository.findById(studyId).get();
        assertEquals(1, study.getStatistics().getCommentCount());
    }
}
