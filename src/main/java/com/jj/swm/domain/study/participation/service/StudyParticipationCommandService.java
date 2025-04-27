package com.jj.swm.domain.study.participation.service;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.participation.constants.StudyParticipationConstants;
import com.jj.swm.domain.study.participation.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.ModifyStudyParticipationLinkRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationStatusRequest;
import com.jj.swm.domain.study.participation.dto.response.UpdateStudyParticipationStatusResponse;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationStatus;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jj.swm.global.common.util.ListCheckUtils.isListNotEmpty;
import static com.jj.swm.global.common.util.ListCheckUtils.isListPresent;

@Service
@RequiredArgsConstructor
public class StudyParticipationCommandService {

    private final UserRepository userRepository;
    private final StudyParticipationRepository participationRepository;
    private final RecruitmentPositionRepository recruitmentPositionRepository;
    private final StudyParticipationLinkRepository participationLinkRepository;

    @Transactional
    public void createStudyParticipation(
            CreateStudyParticipationRequest request,
            Long recruitmentPositionId,
            UUID userId
    ) {
        StudyRecruitmentPosition recruitmentPosition =
                recruitmentPositionRepository.findByIdWithStudy(recruitmentPositionId)
                        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Recruitment Position not found"));

        Study study = recruitmentPosition.getStudy();

        validateAlreadyExistsAndBeforeThreeDays(study, userId);

        validateAcceptedCountNotEqualHeadcount(recruitmentPosition);

        User user = userRepository.getReferenceById(userId);

        StudyParticipation participation = StudyParticipation.of(
                request,
                study,
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

        StudyParticipation participation = participationRepository.findByIdWithStudy(participationId)
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
        StudyParticipation participation =
                findParticipationByIdAndUserIdOrThrowAlsoValidateStatusNotAccepted(participationId, userId);

        modifyLink(request.getModifyLinkRequest(), participation);

        participation.modify(request);
    }

    @Transactional
    public void deleteStudyParticipation(Long participationId, UUID userId) {
        StudyParticipation participation =
                findParticipationByIdAndUserIdOrThrowAlsoValidateStatusNotAccepted(participationId, userId);

        participationLinkRepository.deleteAllByParticipationId(participation.getId());
        participationRepository.delete(participation);
    }

    @Transactional
    public void updateStudyParticipationPosition(
            Long recruitmentPositionId,
            Long participationId,
            UUID userId
    ) {
        StudyParticipation participation =
                findParticipationByIdAndUserIdOrThrowAlsoValidateStatusNotAccepted(participationId, userId);

        StudyRecruitmentPosition recruitmentPosition = recruitmentPositionRepository.findById(recruitmentPositionId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Recruitment Position not found"));

        validateSameStudy(participation, recruitmentPosition);

        validateSameRecruitmentPosition(recruitmentPositionId, participation);

        validateAcceptedCountNotEqualHeadcount(recruitmentPosition);

        participation.modifyPosition(recruitmentPosition);
    }

    private void validateSameRecruitmentPosition(Long recruitmentPositionId, StudyParticipation participation) {
        if (participation.getRecruitmentPosition().getId().equals(recruitmentPositionId)) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Recruitment position same thing");
        }
    }

    private void validateSameStudy(StudyParticipation participation, StudyRecruitmentPosition recruitmentPosition) {
        if (!participation.getStudy().getId().equals(recruitmentPosition.getStudy().getId())) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Recruitment position does not belong to same study");
        }
    }

    private void validateAlreadyExistsAndBeforeThreeDays(Study study, UUID userId) {
        Optional<StudyParticipation> optionalParticipation =
                participationRepository.findByStudyIdAndUserId(study.getId(), userId);
        optionalParticipation.ifPresent(participation -> {
            if (participation.getDeletedAt() == null) {
                throw new GlobalException(ErrorCode.NOT_VALID, "Already Exists");
            } else if (participation.getStatus() == StudyParticipationStatus.REJECTED
                    && LocalDateTime.now().isBefore(participation.getDeletedAt().plusDays(3))) {
                throw new GlobalException(ErrorCode.NOT_VALID, "Three days have not passed yet.");
            }
        });
    }

    private StudyParticipation findParticipationByIdAndUserIdOrThrowAlsoValidateStatusNotAccepted(
            Long participationId, UUID userId
    ) {
        StudyParticipation participation = participationRepository.findByIdAndUserId(participationId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study participation not found"));

        if (participation.getStatus() == StudyParticipationStatus.ACCEPTED) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Already accepted study participation");
        }

        return participation;
    }

    private void modifyLink(ModifyStudyParticipationLinkRequest request, StudyParticipation participation) {
        if (request != null) {
            List<String> linksToAdd = Optional.ofNullable(request.getLinksToAdd())
                    .orElse(Collections.emptyList());
            List<Long> linkIdsToRemove = Optional.ofNullable(request.getLinkIdsToRemove())
                    .orElse(Collections.emptyList());

            long oldLinkSize = participationLinkRepository.countByParticipationId(participation.getId());
            long newLinkSize = oldLinkSize + linksToAdd.size() - linkIdsToRemove.size();

            if (newLinkSize < 0 || newLinkSize > StudyParticipationConstants.LINK_LIMIT) {
                throw new GlobalException(ErrorCode.NOT_VALID, "Link Limit Deviation");
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

            validateAcceptedCountNotEqualHeadcount(recruitmentPosition);
        }
    }

    private void validateStudyWriter(StudyParticipation participation, UUID userId) {
        if (!participation.getStudy().getUser().getId().equals(userId)) {
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

    private void insertLinksIfPresent(List<String> links, StudyParticipation participation) {
        if (isListPresent(links)) {
            participationLinkRepository.batchInsert(links, participation);
        }
    }

    private void validateAcceptedCountNotEqualHeadcount(StudyRecruitmentPosition recruitmentPosition) {
        long acceptedCount =
                participationRepository.countByRecruitmentPositionIdAndAcceptedStatus(recruitmentPosition.getId());

        if (recruitmentPosition.getHeadcount() == acceptedCount) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Recruitment Position already full");
        }
    }
}
