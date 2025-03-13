package com.jj.swm.domain.study.comment.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;
import com.jj.swm.domain.study.comment.dto.response.UpdateCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.fixture.request.CommentRequestFixture;
import com.jj.swm.domain.study.comment.repository.CommentRepository;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.request.StudyRequestFixture;
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
    private Long studyId;
    private Long commentId;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.createUser());
        studyCommandService.addStudy(user.getId(), StudyRequestFixture.buildCreateStudyRequest());
        commentId = commentCommandService.addComment(
                user.getId(),
                studyId,
                null,
                CommentRequestFixture.buildCreateCommentRequest()
        ).getCommentId();
    }

    @Test
    @DisplayName("스터디 모집 댓글 생성에 성공한다.")
    void addComment_Success() {
        //given
        UpsertCommentRequest createRequest = CommentRequestFixture.buildCreateCommentRequest();

        //when
        Long newCommentId = commentCommandService.addComment(
                user.getId(),
                studyId,
                null,
                createRequest
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
    void addComment_WithParentId_Success() {
        //when
        Long replyId = commentCommandService.addComment(
                user.getId(),
                studyId,
                commentId,
                CommentRequestFixture.buildCreateCommentRequest()
        ).getCommentId();

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(1, study.getCommentCount());

        StudyComment reply = commentRepository.findById(replyId).get();
        assertEquals(commentId, reply.getParent().getId());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 id에 대해 대댓글을 생성해도 성공한다.")
    void addComment_WithReplyIdForParent_Success() {
        //given
        UpsertCommentRequest createRequest = CommentRequestFixture.buildCreateCommentRequest();
        Long replyId = commentCommandService.addComment(
                user.getId(),
                studyId,
                commentId,
                createRequest
        ).getCommentId();

        //when
        Long reRePlyId = commentCommandService.addComment(
                user.getId(),
                studyId,
                replyId,
                createRequest
        ).getCommentId();

        //then
        StudyComment reReply = commentRepository.findById(reRePlyId).get();
        assertEquals(commentId, reReply.getParent().getId());
    }

    @Test
    @DisplayName("스터디 모집 댓글 수정에 성공한다.")
    void modifyComment_Success() {
        //given
        UpsertCommentRequest updateRequest = CommentRequestFixture.buildUpdateCommentRequest();

        //when
        UpdateCommentResponse response = commentCommandService.modifyComment(
                user.getId(),
                commentId,
                updateRequest
        );

        //then
        StudyComment comment = commentRepository.findById(commentId).get();

        assertEquals(updateRequest.getContent(), comment.getContent());
        assertNotEquals(comment.getCreatedAt(), response.getUpdatedAt());
    }

    @Test
    @DisplayName("스터디 모집 댓글 삭제에 성공한다.")
    void removeComment_Success() {
        //when
        commentCommandService.removeComment(
                user.getId(),
                studyId,
                commentId
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(0, study.getCommentCount());

        assertEquals(0, commentRepository.count());
    }

    @Test
    @DisplayName("대댓글이 있어도 스터디 모집 댓글 삭제에 성공한다.")
    void removeComment_WithReply_Success() {
        commentCommandService.addComment(
                user.getId(),
                studyId,
                commentId,
                CommentRequestFixture.buildCreateCommentRequest()
        );

        //when
        commentCommandService.removeComment(
                user.getId(),
                studyId,
                commentId
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(0, study.getCommentCount());

        assertEquals(0, commentRepository.count());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 삭제에 성공한다.")
    void removeComment_WithReplyId_Success() {
        //given
        Long replyId = commentCommandService.addComment(
                user.getId(),
                studyId,
                commentId,
                CommentRequestFixture.buildCreateCommentRequest()
        ).getCommentId();

        //when
        commentCommandService.removeComment(
                user.getId(),
                studyId,
                replyId
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(1, study.getCommentCount());

        Optional<StudyComment> optionalComment = commentRepository.findById(replyId);
        assertFalse(optionalComment.isPresent());
    }
}
