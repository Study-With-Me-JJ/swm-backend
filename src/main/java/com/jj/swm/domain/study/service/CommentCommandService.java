package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.CommentCreateRequest;
import com.jj.swm.domain.study.dto.response.CommentCreateResponse;
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
            UUID uuid,
            Long studyId,
            CommentCreateRequest createRequest
    ) {
        User user = getUser(uuid);

        Study study = getStudyPessimisticLock(studyId);

        StudyComment comment = StudyComment.of(
                study,
                user,
                createRequest
        );
        commentRepository.save(comment);

        study.incrementCommentCount();

        return CommentCreateResponse.from(comment);
    }

    private User getUser(UUID userId) {
        return userRepository.getReferenceById(userId);
    }

    private Study getStudyPessimisticLock(Long studyId) {
        return studyRepository.findByIdWithPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

}
