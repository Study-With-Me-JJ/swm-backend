package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.CommentUpsertRequest;
import com.jj.swm.domain.study.dto.response.CommentCreateResponse;
import com.jj.swm.domain.study.dto.response.CommentUpdateResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyComment;
import com.jj.swm.domain.study.repository.CommentRepository;
import com.jj.swm.domain.study.repository.StudyRepository;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
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
        User user = getUser(userId);

        Study study;
        StudyComment parent = null;
        if (parentId != null) {
            study = studyRepository.findById(studyId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
            parent = commentRepository.findWithParentById(parentId, userId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "parent comment not found"));
            parent = parent.getParent() == null ? parent : parent.getParent();
        } else {
            study = studyRepository.getReferenceById(studyId);
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

    private User getUser(UUID userId) {
        return userRepository.getReferenceById(userId);
    }

    private Study getStudyPessimisticLock(Long studyId) {
        return studyRepository.findByIdWithPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }
}
