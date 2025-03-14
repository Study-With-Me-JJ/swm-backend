package com.jj.swm.domain.study.comment.service;

import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;
import com.jj.swm.domain.study.comment.dto.response.CreateCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.UpdateCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.CommentRepository;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentCommandService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CreateCommentResponse createComment(
            UUID userId,
            Long studyId,
            Long parentId,
            UpsertCommentRequest createRequest
    ) {
        User user = userRepository.getReferenceById(userId);

        StudyAndParentComment studyAndParentComment = buildStudyAndParentComment(
                studyId,
                parentId
        );

        StudyComment comment = buildComment(
                user,
                studyAndParentComment,
                createRequest
        );

        commentRepository.save(comment);

        return CreateCommentResponse.from(comment);
    }

    @Transactional
    public UpdateCommentResponse updateComment(
            UUID userId,
            Long commentId,
            UpsertCommentRequest updateRequest
    ) {
        StudyComment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "comment not found"));
        comment.modify(updateRequest);

        return UpdateCommentResponse.from();
    }

    @Transactional
    public void deleteComment(
            UUID userId,
            Long studyId,
            Long commentId
    ) {
        StudyComment comment = commentRepository.findByIdAndUserIdWithParent(commentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "comment not found"));

        decrementCommentCountIfParent(studyId, comment);

        commentRepository.deleteAllByIdOrParentId(commentId);
    }

    private void decrementCommentCountIfParent(Long studyId, StudyComment comment) {
        if (comment.getParent() == null) {
            Study study = findByIdUsingPessimisticLockOrThrow(studyId);
            study.decrementCommentCount();
        }
    }

    private Study findByIdUsingPessimisticLockOrThrow(Long studyId) {
        return studyRepository.findByIdUsingPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private StudyAndParentComment buildStudyAndParentComment(
            Long studyId,
            Long parentId
    ) {
        Study study;
        StudyComment parent = null;

        if (parentId != null) {
            study = studyRepository.findById(studyId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

            parent = commentRepository.findByIdWithParent(parentId)
                    .map(comment -> comment.getParent() == null ? comment : comment.getParent())
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "parent comment not found"));
        } else {
            study = findByIdUsingPessimisticLockOrThrow(studyId);
            study.incrementCommentCount();
        }

        return new StudyAndParentComment(study, parent);
    }

    private StudyComment buildComment(
            User user,
            StudyAndParentComment studyAndParentComment,
            UpsertCommentRequest createRequest
    ) {
        StudyComment comment = StudyComment.of(
                user,
                studyAndParentComment.study(),
                createRequest
        );

        StudyComment parent = studyAndParentComment.parent();
        if (parent != null) {
            comment.addParent(parent);
        }

        return comment;
    }

    private record StudyAndParentComment(Study study, StudyComment parent) {
    }
}
