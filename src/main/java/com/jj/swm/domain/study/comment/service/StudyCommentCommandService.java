package com.jj.swm.domain.study.comment.service;

import com.jj.swm.domain.study.comment.dto.request.UpsertStudyCommentRequest;
import com.jj.swm.domain.study.comment.dto.response.CreateStudyCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.UpdateStudyCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.StudyCommentRepository;
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
public class StudyCommentCommandService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyCommentRepository commentRepository;

    @Transactional
    public CreateStudyCommentResponse createComment(
            UpsertStudyCommentRequest createRequest,
            Long studyId,
            Long parentId,
            UUID userId
    ) {
        User user = userRepository.getReferenceById(userId);
        StudyComment parentComment = findByIdOrThrowIfNotParentElseNull(parentId);
        Study study = findByIdOrThrowIfParentThenUsingLock(studyId, parentId);

        StudyComment comment = buildComment(
                createRequest,
                study,
                parentComment,
                user
        );

        commentRepository.save(comment);

        incrementStudyCommentCountIfParent(study, parentComment);

        return CreateStudyCommentResponse.from(comment);
    }

    @Transactional
    public UpdateStudyCommentResponse updateComment(
            UpsertStudyCommentRequest updateRequest,
            Long commentId,
            UUID userId
    ) {
        StudyComment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "comment not found"));

        comment.modify(updateRequest);

        return UpdateStudyCommentResponse.from();
    }

    @Transactional
    public void deleteComment(Long commentId, UUID userId) {
        StudyComment comment = commentRepository.findByIdAndUserIdWithParent(commentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "comment not found"));

        decrementStudyCommentCountIfParent(comment);

        commentRepository.deleteAllByIdOrParentId(commentId);
    }

    private void decrementStudyCommentCountIfParent(StudyComment comment) {
        if (comment.getParent() == null) {
            Study study = findByIdUsingLockOrThrow(comment.getStudy().getId());
            study.decrementCommentCount();
        }
    }

    private Study findByIdUsingLockOrThrow(Long studyId) {
        return studyRepository.findByIdUsingLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private void incrementStudyCommentCountIfParent(Study study, StudyComment comment) {
        if (comment == null) {
            study.incrementCommentCount();
        }
    }

    private StudyComment buildComment(
            UpsertStudyCommentRequest createRequest,
            Study study,
            StudyComment parentComment,
            User user
    ) {
        StudyComment comment = StudyComment.of(
                createRequest,
                study,
                user
        );

        if (parentComment != null) {
            comment.addParent(parentComment);
        }

        return comment;
    }

    private Study findByIdOrThrowIfParentThenUsingLock(Long studyId, Long parentId) {
        return isParentComment(parentId)
                ? findByIdUsingLockOrThrow(studyId)
                : studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private StudyComment findByIdOrThrowIfNotParentElseNull(Long commentId) {
        return isParentComment(commentId)
                ? null
                : commentRepository.findByIdWithParent(commentId)
                .map(comment -> comment.getParent() == null ? comment : comment.getParent())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "parent comment not found"));
    }

    private boolean isParentComment(Long commentId) {
        return commentId == null;
    }
}
