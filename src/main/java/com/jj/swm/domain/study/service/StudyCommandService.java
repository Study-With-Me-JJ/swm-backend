package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.StudyCreateRequest;
import com.jj.swm.domain.study.dto.response.StudyBookmarkCreateResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyBookmark;
import com.jj.swm.domain.study.repository.*;
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
public class StudyCommandService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyTagRepository studyTagRepository;
    private final StudyImageRepository studyImageRepository;
    private final StudyBookmarkRepository studyBookmarkRepository;
    private final StudyRecruitmentPositionRepository studyRecruitmentPositionRepository;

    @Transactional
    public void create(UUID userId, StudyCreateRequest createRequest) {
        User user = getUser(userId);

        Study study = Study.of(user, createRequest);
        studyRepository.save(study);

        if (createRequest.getTags() != null && !createRequest.getTags().isEmpty()) {
            studyTagRepository.batchInsert(study, createRequest.getTags());
        }

        if (createRequest.getImageUrls() != null && !createRequest.getImageUrls().isEmpty()) {
            studyImageRepository.batchInsert(study, createRequest.getImageUrls());
        }

        if (createRequest.getRecruitPositionsCreateRequests() != null &&
                !createRequest.getRecruitPositionsCreateRequests().isEmpty()
        ) {
            studyRecruitmentPositionRepository.batchInsert(study, createRequest.getRecruitPositionsCreateRequests());
        }
    }

    @Transactional
    public StudyBookmarkCreateResponse createBookmark(UUID userId, Long studyId) {
        User user = getUser(userId);

        Study study = getStudy(studyId);

        StudyBookmark studyBookmark = StudyBookmark.of(study, user);
        studyBookmarkRepository.save(studyBookmark);

        return StudyBookmarkCreateResponse.from(studyBookmark);
    }

    public void deleteBookmark(UUID userId, Long bookmarkId) {
        User user = getUser(userId);

        StudyBookmark studyBookmark = studyBookmarkRepository.findWithUserById(bookmarkId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study bookmark not found"));

        if (!studyBookmark.getUser().getId().equals(user.getId())) {
            throw new GlobalException(ErrorCode.FORBIDDEN, "User does not have permission to delete the bookmark.");
        }
        studyBookmarkRepository.deleteById(studyBookmark.getId());
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "user not found"));
    }

    private Study getStudy(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }
}
