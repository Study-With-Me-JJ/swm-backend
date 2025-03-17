package com.jj.swm.domain.study.recruitmentposition.service;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.response.CreateRecruitmentPositionResponse;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.jj.swm.domain.study.constants.StudyConstants.RECRUITMENT_POSITION_LIMIT;

@Service
@RequiredArgsConstructor
public class RecruitmentPositionCommandService {

    private final StudyRepository studyRepository;
    private final RecruitmentPositionRepository recruitmentPositionRepository;

    @Transactional
    public CreateRecruitmentPositionResponse createRecruitmentPosition(
            CreateRecruitmentPositionRequest request,
            Long studyId,
            UUID userId
    ) {
        validateRecruitmentPositionSizeLimit(studyId);

        Study study = studyRepository.findByIdAndUserId(studyId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        StudyRecruitmentPosition recruitmentPosition = StudyRecruitmentPosition.of(request, study);
        recruitmentPositionRepository.save(recruitmentPosition);

        return CreateRecruitmentPositionResponse.from(recruitmentPosition);
    }

    @Transactional
    public void updateRecruitmentPosition(
            UpdateRecruitmentPositionRequest request,
            Long recruitmentPositionId,
            UUID userId
    ) {
        StudyRecruitmentPosition recruitmentPosition = findByIdAndUserIdOrThrow(recruitmentPositionId, userId);

        validateAcceptedCount(request);

        recruitmentPosition.modify(request);
    }

    @Transactional
    public void deleteRecruitmentPosition(Long recruitmentPositionId, UUID userId) {
        StudyRecruitmentPosition recruitmentPosition = findByIdAndUserIdOrThrow(recruitmentPositionId, userId);

        recruitmentPositionRepository.delete(recruitmentPosition);
    }

    private void validateRecruitmentPositionSizeLimit(Long studyId) {
        int recruitmentPositionSize = recruitmentPositionRepository.countByStudyId(studyId);
        if (recruitmentPositionSize + 1 > RECRUITMENT_POSITION_LIMIT) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Recruitment Position Limit Exceeded");
        }
    }

    private void validateAcceptedCount(UpdateRecruitmentPositionRequest request) {
        if (request.getHeadcount() < request.getAcceptedCount()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "The number of accepted exceeds the recruitment limit.");
        }
    }

    private StudyRecruitmentPosition findByIdAndUserIdOrThrow(Long recruitmentPositionId, UUID userId) {
        return recruitmentPositionRepository.findByIdAndStudyUserId(recruitmentPositionId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "recruitment position not found"));
    }
}
