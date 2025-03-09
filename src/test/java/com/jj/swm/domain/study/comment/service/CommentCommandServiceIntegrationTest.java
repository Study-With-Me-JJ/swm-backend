package com.jj.swm.domain.study.comment.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;
import com.jj.swm.domain.study.comment.dto.response.CreateCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.UpdateCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.fixture.request.CommentRequestFixture;
import com.jj.swm.domain.study.comment.repository.CommentRepository;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.entity.StudyFixture;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    // service
    @Autowired
    private CommentCommandService commentCommandService;

    // repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private StudyRepository studyRepository;

    // entity
    private User user;
    private Study study;
    private Long commentId;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.createUser());
        study = studyRepository.save(StudyFixture.buildStudy(user));
        commentId = commentCommandService.addComment(
                user.getId(),
                study.getId(),
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
        CreateCommentResponse response = commentCommandService.addComment(
                user.getId(),
                study.getId(),
                null,
                createRequest
        );

        //then
        study = studyRepository.findById(study.getId()).get();
        assertEquals(2, study.getCommentCount());

        assertEquals(2L, response.getCommentId());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 생성에 성공한다.")
    void addComment_WithParentId_Success() {
        //given
        UpsertCommentRequest createRequest = CommentRequestFixture.buildCreateCommentRequest();

        //when
        CreateCommentResponse response = commentCommandService.addComment(
                user.getId(),
                study.getId(),
                commentId,
                createRequest
        );

        //then
        study = studyRepository.findById(study.getId()).get();
        assertEquals(1, study.getCommentCount());

        assertEquals(2L, response.getCommentId());

        StudyComment reply = commentRepository.findById(2L).get();
        assertEquals(commentId, reply.getParent().getId());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 id에 대해 대댓글을 생성해도 성공한다.")
    void addComment_WithReplyIdForParent_Success() {
        //given
        UpsertCommentRequest createRequest = CommentRequestFixture.buildCreateCommentRequest();
        Long replyId = commentCommandService.addComment(
                user.getId(),
                study.getId(),
                commentId,
                createRequest
        ).getCommentId();

        //when
        CreateCommentResponse response = commentCommandService.addComment(
                user.getId(),
                study.getId(),
                replyId,
                createRequest
        );

        //then
        study = studyRepository.findById(study.getId()).get();
        assertEquals(1, study.getCommentCount());

        assertEquals(3L, response.getCommentId());

        StudyComment reply = commentRepository.findById(3L).get();
        assertEquals(commentId, reply.getParent().getId());
    }

    @Test
    @DisplayName("스터디 모집 댓글 수정에 성공한다.")
    void modifyComment_Success() {
        //given
        UpsertCommentRequest updateRequest = CommentRequestFixture.buildUpdateCommentRequest();

        //when
        UpdateCommentResponse response =
                commentCommandService.modifyComment(user.getId(), commentId, updateRequest);

        //then
        StudyComment comment = commentRepository.findById(commentId).get();

        assertEquals(updateRequest.getContent(), comment.getContent());
        assertNotEquals(comment.getCreatedAt(), response.getUpdatedAt());
    }

    @Test
    @DisplayName("스터디 모집 댓글 삭제에 성공한다.")
    void removeComment_Success() {
        //when
        commentCommandService.removeComment(user.getId(), study.getId(), commentId);

        //then
        study = studyRepository.findById(study.getId()).get();
        assertEquals(0, study.getCommentCount());

        assertEquals(0, commentRepository.count());
    }

    @Test
    @DisplayName("대댓글이 있어도 스터디 모집 댓글 삭제에 성공한다.")
    void removeComment_WithReply_Success() {
        UpsertCommentRequest createRequest = CommentRequestFixture.buildCreateCommentRequest();
        commentCommandService.addComment(
                user.getId(),
                study.getId(),
                commentId,
                createRequest
        );

        //when
        commentCommandService.removeComment(user.getId(), study.getId(), commentId);

        //then
        study = studyRepository.findById(study.getId()).get();
        assertEquals(0, study.getCommentCount());

        assertEquals(0, commentRepository.count());
    }

    @Test
    @DisplayName("스터디 모집 대댓글 삭제에 성공한다.")
    void removeComment_WithReplyId_Success() {
        //given
        UpsertCommentRequest createRequest = CommentRequestFixture.buildCreateCommentRequest();
        Long replyId = commentCommandService.addComment(
                user.getId(),
                study.getId(),
                commentId,
                createRequest
        ).getCommentId();

        //when
        commentCommandService.removeComment(user.getId(), study.getId(), replyId);

        //then
        study = studyRepository.findById(study.getId()).get();
        Optional<StudyComment> optionalComment = commentRepository.findById(replyId);

        assertEquals(1, study.getCommentCount());
        assertFalse(optionalComment.isPresent());


    }
}
