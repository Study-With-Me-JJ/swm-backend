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
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.jj.swm.global.common.enums.ErrorCode.NOT_FOUND;

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
        StudyComment parent = findByIdIfNotParentElseNull(parentId);
        Study study = findByIdIfParentThenUsingLock(studyId, parentId);

        StudyComment comment = buildComment(
                createRequest,
                study,
                parent,
                user
        );

        commentRepository.save(comment);

        increaseStudyCommentCountIfParent(study, parent);

        return CreateStudyCommentResponse.from(comment);
    }

    @Transactional
    public UpdateStudyCommentResponse updateComment(
            UpsertStudyCommentRequest updateRequest,
            Long commentId,
            UUID userId
    ) {
        StudyComment comment = findByIdAndUserId(commentId, userId);

        comment.modify(updateRequest);

        return UpdateStudyCommentResponse.from();
    }

    @Transactional
    public void deleteComment(Long commentId, UUID userId) {
        StudyComment comment = findByIdAndUserId(commentId, userId);

        decreaseStudyCommentCountIfParent(comment);

        commentRepository.deleteWithChildrenById(commentId);
    }

    private StudyComment findByIdAndUserId(Long commentId, UUID userId) {
        return commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "comment not found"));
    }

    private void decreaseStudyCommentCountIfParent(StudyComment comment) {
        if (comment.getParent() == null) {
            Study study = findByIdUsingLock(comment.getStudy().getId());
            study.decreaseCommentCount();
        }
    }

    private Study findByIdUsingLock(Long studyId) {
        return studyRepository.findByIdUsingLock(studyId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study not found"));
    }

    private void increaseStudyCommentCountIfParent(Study study, StudyComment parent) {
        if (parent == null) {
            study.increaseCommentCount();
        }
    }

    private StudyComment buildComment(
            UpsertStudyCommentRequest createRequest,
            Study study,
            StudyComment parent,
            User user
    ) {
        StudyComment comment = StudyComment.of(
                createRequest,
                study,
                user
        );

        if (parent != null) {
            comment.setParent(parent);
        }

        return comment;
    }

    private Study findByIdIfParentThenUsingLock(Long studyId, Long parentId) {
        return parentId == null
                ? findByIdUsingLock(studyId)
                : studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study not found"));
    }

    private StudyComment findByIdIfNotParentElseNull(Long parentId) {
        return parentId == null
                ? null
                : commentRepository.findByIdWithParent(parentId)
                .map(comment -> comment.getParent() == null ? comment : comment.getParent())
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "parent comment not found"));
    }
}
