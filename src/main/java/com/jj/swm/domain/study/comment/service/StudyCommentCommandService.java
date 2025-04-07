package com.jj.swm.domain.study.comment.service;

import com.jj.swm.domain.study.comment.dto.request.UpsertStudyCommentRequest;
import com.jj.swm.domain.study.comment.dto.response.CreateStudyCommentResponse;
import com.jj.swm.domain.study.comment.dto.response.UpdateStudyCommentResponse;
import com.jj.swm.domain.study.comment.entity.StudyComment;
import com.jj.swm.domain.study.comment.repository.StudyStudyCommentRepository;
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
    private final StudyStudyCommentRepository commentRepository;

    @Transactional
    public CreateStudyCommentResponse createComment(
            UpsertStudyCommentRequest createRequest,
            Long studyId,
            Long parentCommentId,
            UUID userId
    ) {
        User user = userRepository.getReferenceById(userId);
        StudyComment parentComment = findByIdOrThrowIfNotParentElseNull(parentCommentId);
        Study study = findByIdOrThrowIfParentThenUsingLock(studyId, parentCommentId);

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
    public void deleteComment(
            Long studyId,
            Long commentId,
            UUID userId
    ) {
        StudyComment comment = commentRepository.findByIdAndUserIdWithParent(commentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "comment not found"));

        if (comment.getParent() == null) {
            Study study = findByIdUsingLockOrThrow(studyId);
            study.decrementCommentCount();
        }

        commentRepository.deleteAllByIdOrParentId(commentId);
    }

    private Study findByIdUsingLockOrThrow(Long studyId) {
        return studyRepository.findByIdUsingLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private void incrementStudyCommentCountIfParent(Study study, StudyComment parentComment) {
        if (parentComment == null) {
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

    private Study findByIdOrThrowIfParentThenUsingLock(Long studyId, Long parentCommentId) {
        return isParentComment(parentCommentId)
                ? findByIdUsingLockOrThrow(studyId)
                : studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private StudyComment findByIdOrThrowIfNotParentElseNull(Long parentCommentId) {
        return isParentComment(parentCommentId)
                ? null
                : commentRepository.findByIdWithParent(parentCommentId)
                .map(comment -> comment.getParent() == null ? comment : comment.getParent())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "parent comment not found"));
    }

    private boolean isParentComment(Long parentCommentId) {
        return parentCommentId == null;
    }
}
