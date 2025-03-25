package com.jj.swm.domain.study.recruitmentposition.service;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.recruitmentposition.constants.StudyParticipationConstants;
import com.jj.swm.domain.study.recruitmentposition.dto.request.*;
import com.jj.swm.domain.study.recruitmentposition.dto.response.CreateRecruitmentPositionResponse;
import com.jj.swm.domain.study.recruitmentposition.dto.response.UpdateStudyParticipationStatusResponse;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationStatus;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.recruitmentposition.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.recruitmentposition.repository.StudyParticipationRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jj.swm.domain.study.constants.StudyConstants.RECRUITMENT_POSITION_LIMIT;
import static com.jj.swm.global.common.util.ListCheckUtils.*;

@Service
@RequiredArgsConstructor
public class RecruitmentPositionCommandService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyParticipationRepository participationRepository;
    private final RecruitmentPositionRepository recruitmentPositionRepository;
    private final StudyParticipationLinkRepository participationLinkRepository;

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

        validateAcceptedCountLessThanHeadcount(
                request, participationRepository.countByRecruitmentPositionIdAndAcceptedStatus(recruitmentPositionId)
        );

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

        validateAcceptedCountNotEqualHeadcount(
                recruitmentPosition,
                participationRepository.countByRecruitmentPositionIdAndAcceptedStatus(recruitmentPositionId)
        );

        User user = userRepository.getReferenceById(userId);

        StudyParticipation participation = StudyParticipation.of(
                request,
                recruitmentPosition,
                user
        );
        participationRepository.save(participation);

        insertLinksIfPresent(request.getLinks(), participation);
    }

    @Transactional
    public UpdateStudyParticipationStatusResponse updateStudyParticipationStatus(
            UpdateStudyParticipationStatusRequest request,
            Long participationId,
            UUID userId
    ) {
        StudyParticipationStatus newStatus = request.getStatus();

        validateNewStatusNotPending(newStatus);

        StudyParticipation participation = participationRepository.findByIdWithRecruitmentAndStudy(participationId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study participation not found"));

        validateOldStatusMustPending(participation);

        validateStudyWriter(participation, userId);

        validateAcceptedCountNotEqualsHeadcountIfAcceptedStatus(participation, newStatus);

        participation.modifyStatus(newStatus);

        return buildUpdateStudyParticipationStatusResponse(participation);
    }

    @Transactional
    public void updateStudyParticipation(
            UpdateStudyParticipationRequest request,
            Long participationId,
            UUID userId
    ) {
        StudyParticipation participation = participationRepository.findByIdAndUserId(participationId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study participation not found"));

        modifyLink(request.getModifyLinkRequest(), participation);

        participation.modify(request);
    }

    private void modifyLink(ModifyStudyParticipationLinkRequest request, StudyParticipation participation) {
        if (request != null) {
            List<String> linksToAdd = Optional.ofNullable(request.getLinksToAdd())
                    .orElse(Collections.emptyList());
            List<Long> linkIdsToRemove = Optional.ofNullable(request.getLinkIdsToRemove())
                    .orElse(Collections.emptyList());

            int oldLinkSize = participationLinkRepository.countByParticipationId(participation.getId());
            int newLinkSize = oldLinkSize + linksToAdd.size() - linkIdsToRemove.size();

            if (newLinkSize < 0 || newLinkSize > StudyParticipationConstants.LINK_LIMIT) {
                throw new GlobalException(ErrorCode.NOT_VALID, "Link Limit Exceeded");
            }

            if (isListNotEmpty(linksToAdd))
                participationLinkRepository.batchInsert(linksToAdd, participation);

            if (isListNotEmpty(linkIdsToRemove))
                participationLinkRepository.deleteAllByIdsAndParticipationId(
                        linkIdsToRemove, participation.getId()
                );
        }
    }

    private UpdateStudyParticipationStatusResponse buildUpdateStudyParticipationStatusResponse(
            StudyParticipation participation
    ) {
        if (participation.getStatus() == StudyParticipationStatus.ACCEPTED) {
            return UpdateStudyParticipationStatusResponse.from(participation);
        }

        return null;
    }

    private void validateAcceptedCountNotEqualsHeadcountIfAcceptedStatus(
            StudyParticipation participation,
            StudyParticipationStatus status
    ) {
        if (status == StudyParticipationStatus.ACCEPTED) {
            StudyRecruitmentPosition recruitmentPosition = participation.getRecruitmentPosition();

            int acceptedCount =
                    participationRepository.countByRecruitmentPositionIdAndAcceptedStatus(recruitmentPosition.getId());

            if (acceptedCount == recruitmentPosition.getHeadcount()) {
                throw new GlobalException(ErrorCode.NOT_VALID, "Recruitment Position already full");
            }
        }
    }

    private void validateStudyWriter(StudyParticipation participation, UUID userId) {
        if (!participation.getRecruitmentPosition().getStudy().getUser().getId().equals(userId)) {
            throw new GlobalException(ErrorCode.FORBIDDEN, "not study writer");
        }
    }

    private void validateOldStatusMustPending(StudyParticipation participation) {
        if (participation.getStatus() != StudyParticipationStatus.PENDING) {
            throw new GlobalException(ErrorCode.NOT_VALID, "already changed status");
        }
    }

    private void validateNewStatusNotPending(StudyParticipationStatus status) {
        if (status == StudyParticipationStatus.PENDING) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Invalid status value");
        }
    }

    private void validateRecruitmentPositionSizeLimit(Long studyId) {
        int recruitmentPositionSize = recruitmentPositionRepository.countByStudyId(studyId);
        if (recruitmentPositionSize + 1 > RECRUITMENT_POSITION_LIMIT) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Recruitment Position Limit Exceeded");
        }
    }

    private void validateAcceptedCountLessThanHeadcount(UpsertRecruitmentPositionRequest request, int acceptedCount) {
        if (acceptedCount > request.getHeadcount()) {
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

    private static void validateAcceptedCountNotEqualHeadcount(
            StudyRecruitmentPosition recruitmentPosition,
            int acceptedCount
    ) {
        if (recruitmentPosition.getHeadcount() == acceptedCount) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Recruitment Position already full");
        }
    }
}
