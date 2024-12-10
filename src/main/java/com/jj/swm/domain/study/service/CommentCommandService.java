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

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentCommandService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentCreateResponse create(
            UUID uuid,
            Long studyId,
            Long parentId,
            CommentUpsertRequest createRequest
    ) {
        User user = getUser(uuid);

        Study study = getStudyPessimisticLock(studyId);

        Optional<StudyComment> optionalComment = commentRepository.findById(parentId);

        StudyComment comment = StudyComment.of(
                study,
                user,
                createRequest
        );
        optionalComment.ifPresent(comment::addParent);
        commentRepository.save(comment);

        study.incrementCommentCount();

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
