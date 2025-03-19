package com.jj.swm.domain.study.recruitmentposition.service;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpsertRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.response.CreateRecruitmentPositionResponse;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.recruitmentposition.repository.StudyParticipationAttachmentRepository;
import com.jj.swm.domain.study.recruitmentposition.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.recruitmentposition.repository.StudyParticipationRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.jj.swm.domain.study.constants.StudyConstants.RECRUITMENT_POSITION_LIMIT;
import static com.jj.swm.global.common.util.ListCheckUtils.isListPresent;

@Service
@RequiredArgsConstructor
public class RecruitmentPositionCommandService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyParticipationRepository participationRepository;
    private final RecruitmentPositionRepository recruitmentPositionRepository;
    private final StudyParticipationLinkRepository participationLinkRepository;
    private final StudyParticipationAttachmentRepository participationAttachmentRepository;

    @Transactional
    public CreateRecruitmentPositionResponse createRecruitmentPosition(
            UpsertRecruitmentPositionRequest request,
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
            UpsertRecruitmentPositionRequest request,
            Long recruitmentPositionId,
            UUID userId
    ) {
        StudyRecruitmentPosition recruitmentPosition = findByIdAndUserIdOrThrow(recruitmentPositionId, userId);

        validateAcceptedCountLessThanHeadcount(request, recruitmentPosition);

        recruitmentPosition.modify(request);
    }

    @Transactional
    public void deleteRecruitmentPosition(Long recruitmentPositionId, UUID userId) {
        StudyRecruitmentPosition recruitmentPosition = findByIdAndUserIdOrThrow(recruitmentPositionId, userId);

        recruitmentPositionRepository.delete(recruitmentPosition);
    }

    @Transactional
    public void createStudyParticipation(
            CreateStudyParticipationRequest request,
            Long recruitmentPositionId,
            UUID userId
    ) {
        StudyRecruitmentPosition recruitmentPosition = recruitmentPositionRepository.findById(recruitmentPositionId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Recruitment Position not found"));

        validateAcceptedCountNotEqualHeadcount(recruitmentPosition);

        User user = userRepository.getReferenceById(userId);

        StudyParticipation participation = StudyParticipation.of(
                request,
                recruitmentPosition,
                user
        );
        participationRepository.save(participation);

        insertLinksIfPresent(request.getLinks(), participation);

        insertFileUrlsIfPresent(request.getFileUrls(), participation);
    }

    private void validateRecruitmentPositionSizeLimit(Long studyId) {
        int recruitmentPositionSize = recruitmentPositionRepository.countByStudyId(studyId);
        if (recruitmentPositionSize + 1 > RECRUITMENT_POSITION_LIMIT) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Recruitment Position Limit Exceeded");
        }
    }

    private void validateAcceptedCountLessThanHeadcount(
            UpsertRecruitmentPositionRequest request, StudyRecruitmentPosition recruitmentPosition
    ) {
        if (recruitmentPosition.getAcceptedCount() > request.getHeadcount()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "accepted count is greater than headcount");
        }
    }

    private StudyRecruitmentPosition findByIdAndUserIdOrThrow(Long recruitmentPositionId, UUID userId) {
        return recruitmentPositionRepository.findByIdAndStudyUserId(recruitmentPositionId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "recruitment position not found"));
    }

    private void insertLinksIfPresent(List<String> links, StudyParticipation participation) {
        if (isListPresent(links)) {
            participationLinkRepository.batchInsert(links, participation);
        }
    }

    private void insertFileUrlsIfPresent(List<String> fileUrls, StudyParticipation participation) {
        if (isListPresent(fileUrls)) {
            participationAttachmentRepository.batchInsert(fileUrls, participation);
        }
    }

    private static void validateAcceptedCountNotEqualHeadcount(StudyRecruitmentPosition recruitmentPosition) {
        if (recruitmentPosition.getHeadcount() == recruitmentPosition.getAcceptedCount()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Recruitment Position already full");
        }
    }
}
