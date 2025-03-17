package com.jj.swm.domain.study.comment.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;
import com.jj.swm.domain.study.comment.dto.response.UpdateCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.fixture.dto.request.UpsertCommentRequestFixture;
import com.jj.swm.domain.study.comment.repository.CommentRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CommentCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    // target service
    @Autowired
    private CommentCommandService commentCommandService;

    // service
    @Autowired
    private StudyCommandService studyCommandService;

    // repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private StudyRepository studyRepository;

    // entity
    private User user;
    private Long commentId;
    private final Long studyId = 1L;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.createUser());
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        commentId = commentCommandService.createComment(
                UpsertCommentRequestFixture.create(),
                studyId,
                null,
                user.getId()
        ).getCommentId();
    }

    @Test
    @DisplayName("스터디 모집 댓글 생성에 성공한다.")
    void createComment_Success() {
        //given
        UpsertCommentRequest createRequest = UpsertCommentRequestFixture.create();

        //when
        Long newCommentId = commentCommandService.createComment(
                createRequest,
                studyId,
                null,
                user.getId()
        ).getCommentId();

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(2, study.getCommentCount());

        Optional<StudyComment> optionalComment = commentRepository.findById(newCommentId);
        assertTrue(optionalComment.isPresent());

        StudyComment comment = optionalComment.get();
        assertEquals(createRequest.getContent(), comment.getContent());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 생성에 성공한다.")
    void createComment_WithParentId_Success() {
        //when
        Long replyId = commentCommandService.createComment(
                UpsertCommentRequestFixture.create(),
                studyId,
                commentId,
                user.getId()
        ).getCommentId();

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(1, study.getCommentCount());

        StudyComment reply = commentRepository.findById(replyId).get();
        assertEquals(commentId, reply.getParent().getId());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 id에 대해 대댓글을 생성해도 성공한다.")
    void createComment_WithReplyIdForParent_Success() {
        //given
        UpsertCommentRequest createRequest = UpsertCommentRequestFixture.create();
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
    @DisplayName("스터디 모집 댓글 수정에 성공한다.")
    void updateComment_Success() {
        //given
        UpsertCommentRequest updateRequest = UpsertCommentRequestFixture.update();

        //when
        UpdateCommentResponse response = commentCommandService.updateComment(
                updateRequest,
                commentId,
                user.getId()
        );

        //then
        StudyComment comment = commentRepository.findById(commentId).get();

        assertEquals(updateRequest.getContent(), comment.getContent());
        assertNotEquals(comment.getCreatedAt(), response.getUpdatedAt());
    }

    @Test
    @DisplayName("스터디 모집 댓글 삭제에 성공한다.")
    void deleteComment_Success() {
        //when
        commentCommandService.deleteComment(
                studyId,
                commentId,
                user.getId()
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(0, study.getCommentCount());

        assertEquals(0, commentRepository.count());
    }

    @Test
    @DisplayName("대댓글이 있어도 스터디 모집 댓글 삭제에 성공한다.")
    void deleteComment_WithReply_Success() {
        commentCommandService.createComment(
                UpsertCommentRequestFixture.create(),
                studyId,
                commentId,
                user.getId()
        );

        //when
        commentCommandService.deleteComment(
                studyId,
                commentId,
                user.getId()
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(0, study.getCommentCount());

        assertEquals(0, commentRepository.count());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 삭제에 성공한다.")
    void deleteComment_WithReplyId_Success() {
        //given
        Long replyId = commentCommandService.createComment(
                UpsertCommentRequestFixture.create(),
                studyId,
                commentId,
                user.getId()
        ).getCommentId();

        //when
        commentCommandService.deleteComment(
                studyId,
                replyId,
                user.getId()
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(1, study.getCommentCount());

        Optional<StudyComment> optionalComment = commentRepository.findById(replyId);
        assertFalse(optionalComment.isPresent());
    }
}
