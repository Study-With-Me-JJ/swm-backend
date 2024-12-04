package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.StudyCreateRequest;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.repository.StudyImageRepository;
import com.jj.swm.domain.study.repository.StudyRecruitmentPositionRepository;
import com.jj.swm.domain.study.repository.StudyRepository;
import com.jj.swm.domain.study.repository.StudyTagRepository;
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

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "user not found"));
    }
}
