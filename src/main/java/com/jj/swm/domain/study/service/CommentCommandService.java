package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.CommentUpsertRequest;
import com.jj.swm.domain.study.dto.response.CommentCreateResponse;
import com.jj.swm.domain.study.dto.response.CommentUpdateResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyComment;
import com.jj.swm.domain.study.repository.CommentRepository;
import com.jj.swm.domain.study.repository.StudyRepository;
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
    public CommentCreateResponse create(
            UUID userId,
            Long studyId,
            Long parentId,
            CommentUpsertRequest createRequest
    ) {
        User user = userRepository.getReferenceById(userId);

        Study study;
        StudyComment parent = null;
        if (parentId != null) {
            study = studyRepository.findById(studyId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
            parent = getParent(userId, parentId);
        } else {
            study = getStudyPessimisticLock(studyId);
            study.incrementCommentCount();
        }

        StudyComment comment = StudyComment.of(
                study,
                user,
                createRequest
        );

        if (parent != null) {
            comment.addParent(parent);
        }

        commentRepository.save(comment);

        return CommentCreateResponse.from(comment);
    }

    @Transactional
    public CommentUpdateResponse update(
            UUID userId,
            Long commentId,
            CommentUpsertRequest updateRequest
    ) {
        StudyComment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "comment not found"));
        comment.modify(updateRequest);

        return CommentUpdateResponse.from(comment);
    }

    @Transactional
    public void delete(
            UUID userId,
            Long studyId,
            Long commentId
    ) {
        StudyComment comment = commentRepository.findWithParentByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "comment not found"));

        if (comment.getParent() == null) {
            Study study = getStudyPessimisticLock(studyId);
            study.decrementCommentCount();
        }

        commentRepository.deleteAllByIdOrParentId(commentId);
    }

    private Study getStudyPessimisticLock(Long studyId) {
        return studyRepository.findByIdWithUserUsingPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private StudyComment getParent(UUID userId, Long parentId) {
        StudyComment parent = commentRepository.findWithParentById(parentId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "parent comment not found"));

        return parent.getParent() == null ? parent : parent.getParent();
    }
}
