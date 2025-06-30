package com.jj.swm.domain.study.comment.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.request.UpsertStudyCommentRequest;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.fixture.dto.request.UpsertStudyCommentRequestFixture;
import com.jj.swm.domain.study.comment.repository.StudyCommentRepository;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.core.support.StudyTestRepository;
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
    private StudyTestRepository studyRepository;

    @Autowired
    private StudyCommentRepository commentRepository;

    // entity
    private User user;
    private Long parentId;
    private Study study;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());

        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        Study oldStudy = studyRepository.findFirstByOrderByCreatedAt().orElseThrow();

        parentId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                oldStudy.getId(),
                null,
                user.getId()
        ).getCommentId();

        study = studyRepository.findById(oldStudy.getId()).orElseThrow();
    }

    @Test
    @DisplayName("스터디 모집 댓글 생성에 성공한다.")
    void createComment_Success() {
        //given
        UpsertStudyCommentRequest createRequest = UpsertStudyCommentRequestFixture.create();

        int oldCommentCount = study.getStatistics().getCommentCount();

        //when
        Long newParentId = commentCommandService.createComment(
                createRequest,
                study.getId(),
                null,
                user.getId()
        ).getCommentId();

        //then
        Study updatedStudy = studyRepository.findById(study.getId()).orElseThrow();
        assertEquals(oldCommentCount + 1, updatedStudy.getStatistics().getCommentCount());

        Optional<StudyComment> optionalComment = commentRepository.findById(newParentId);
        assertTrue(optionalComment.isPresent());

        StudyComment comment = optionalComment.get();
        assertEquals(createRequest.getContent(), comment.getContent());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 생성에 성공한다.")
    void createComment_WhenChild_Success() {
        //given
        int oldCommentCount = study.getStatistics().getCommentCount();

        //when
        Long childId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
                parentId,
                user.getId()
        ).getCommentId();

        //then
        Study reloadedStudy = studyRepository.findById(study.getId()).orElseThrow();
        assertEquals(oldCommentCount, reloadedStudy.getStatistics().getCommentCount());

        StudyComment child = commentRepository.findById(childId).orElseThrow();
        assertEquals(parentId, child.getParent().getId());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 id에 대해 대댓글을 생성해도 성공한다.")
    void createComment_WhenChildWithChildId_Success() {
        //given
        UpsertStudyCommentRequest createRequest = UpsertStudyCommentRequestFixture.create();
        Long childId = commentCommandService.createComment(
                createRequest,
                study.getId(),
                parentId,
                user.getId()
        ).getCommentId();

        //when
        Long newChildId = commentCommandService.createComment(
                createRequest,
                study.getId(),
                childId,
                user.getId()
        ).getCommentId();

        //then
        StudyComment newChild = commentRepository.findById(newChildId).orElseThrow();
        assertEquals(parentId, newChild.getParent().getId());
    }

    @Test
    @DisplayName("댓글 생성 시 스터디 모집 댓글 수 동시성 제어에 성공한다.")
    void createComment_Concurrency_Success() throws InterruptedException {
        //given
        int oldCommentCount = study.getStatistics().getCommentCount();
        System.out.println(parentId);

        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    commentCommandService.createComment(
                            UpsertStudyCommentRequestFixture.create(),
                            study.getId(),
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
        Study updatedStudy = studyRepository.findById(study.getId()).orElseThrow();
        assertEquals(oldCommentCount + THREAD_COUNT, updatedStudy.getStatistics().getCommentCount());
    }

    @Test
    @DisplayName("스터디 모집 댓글 수정에 성공한다.")
    void updateComment_Success() {
        //given
        UpsertStudyCommentRequest updateRequest = UpsertStudyCommentRequestFixture.update();

        //when
        commentCommandService.updateComment(
                updateRequest,
                parentId,
                user.getId()
        );

        //then
        StudyComment parent = commentRepository.findById(parentId).orElseThrow();

        assertEquals(updateRequest.getContent(), parent.getContent());
        assertNotEquals(parent.getCreatedAt(), parent.getUpdatedAt());
    }

    @Test
    @DisplayName("스터디 모집 댓글 삭제에 성공한다.")
    void deleteComment_Success() {
        //given
        Long childId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
                parentId,
                user.getId()
        ).getCommentId();

        int oldCommentCount = study.getStatistics().getCommentCount();

        //when
        commentCommandService.deleteComment(parentId, user.getId());

        //then
        Study updatedStudy = studyRepository.findById(study.getId()).orElseThrow();
        assertEquals(oldCommentCount - 1, updatedStudy.getStatistics().getCommentCount());

        assertFalse(commentRepository.findById(parentId).isPresent());
        assertFalse(commentRepository.findById(childId).isPresent());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 삭제에 성공한다.")
    void deleteComment_WhenReply_Success() {
        //given
        Long childId = commentCommandService.createComment(
                UpsertStudyCommentRequestFixture.create(),
                study.getId(),
                parentId,
                user.getId()
        ).getCommentId();

        //when
        commentCommandService.deleteComment(childId, user.getId());

        //then
        assertFalse(commentRepository.findById(childId).isPresent());
    }

    @Test
    @DisplayName("댓글 삭제 시 스터디 모집 댓글 수 동시성 제어에 성공한다.")
    void deleteStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        List<Long> parentIds = new ArrayList<>();
        parentIds.add(parentId);

        for (int i = 0; i < THREAD_COUNT; i++) {
            parentIds.add(commentCommandService.createComment(
                    UpsertStudyCommentRequestFixture.create(),
                    study.getId(),
                    null,
                    user.getId()
            ).getCommentId());
        }

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            Long commentId = parentIds.get(i);
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
        Study updatedStudy = studyRepository.findById(study.getId()).orElseThrow();
        assertEquals(parentIds.size() - THREAD_COUNT, updatedStudy.getStatistics().getCommentCount());
    }
}
