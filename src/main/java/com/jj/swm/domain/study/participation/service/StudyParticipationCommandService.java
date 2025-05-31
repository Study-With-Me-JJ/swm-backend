package com.jj.swm.domain.study.participation.service;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.participation.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationRequest.ModifyLinkInfo;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationStatusRequest;
import com.jj.swm.domain.study.participation.dto.response.UpdateStudyParticipationStatusResponse;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import com.jj.swm.domain.study.participation.entity.StudyParticipationLink;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jj.swm.domain.study.common.EntityModificationValidator.*;
import static com.jj.swm.domain.study.participation.constants.StudyParticipationConstants.LINK_LIMIT;
import static com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus.*;
import static com.jj.swm.global.common.enums.ErrorCode.*;
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
        StudyRecruitmentPosition recruitmentPosition = findById(recruitmentPositionId);

        Study study = recruitmentPosition.getStudy();

        validateUserCanJoinStudy(study, userId);

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

        StudyParticipation participation = participationRepository.findByIdWithStudyAndPosition(participationId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study participation not found"));

        validateOldStatusMustPending(participation);

        validateStudyWriter(participation.getStudy(), userId);

        validateAcceptedCountNotEqualsHeadcountIfAcceptedStatus(participation.getRecruitmentPosition(), newStatus);

        participation.modifyStatus(newStatus);

        return buildUpdateStudyParticipationStatusResponse(participation);
    }

    @Transactional
    public void updateStudyParticipation(
            UpdateStudyParticipationRequest request,
            Long participationId,
            UUID userId
    ) {
        StudyParticipation participation = findNotAcceptedParticipationByIdAndUserId(participationId, userId);

        modifyLinks(request.getModifyLinkInfo(), participation);

        participation.modify(request);
    }

    @Transactional
    public void updateStudyParticipationPosition(
            Long recruitmentPositionId,
            Long participationId,
            UUID userId
    ) {
        StudyParticipation participation = findNotAcceptedParticipationByIdAndUserId(participationId, userId);

        StudyRecruitmentPosition recruitmentPosition = findById(recruitmentPositionId);

        validateSameStudy(participation, recruitmentPosition);

        validateAcceptedCountNotEqualHeadcount(recruitmentPosition);

        participation.modifyPosition(recruitmentPosition);
    }

    @Transactional
    public void deleteStudyParticipation(Long participationId, UUID userId) {
        StudyParticipation participation = findNotAcceptedParticipationByIdAndUserId(participationId, userId);

        participationLinkRepository.deleteAllByParticipationId(participation.getId());
        participationRepository.delete(participation);
    }

    private void validateSameStudy(StudyParticipation participation, StudyRecruitmentPosition recruitmentPosition) {
        if (!participation.getStudy().getId().equals(recruitmentPosition.getStudy().getId())) {
            throw new GlobalException(NOT_VALID, "Recruitment position does not belong to same study");
        }
    }

    private void validateUserCanJoinStudy(Study study, UUID userId) {
        Optional<StudyParticipation> optionalParticipation =
                participationRepository.findByStudyIdAndUserIdWithNativeQuery(study.getId(), userId);

        optionalParticipation.ifPresent(participation -> {
            if (participation.getDeletedAt() == null) {
                throw new GlobalException(NOT_VALID, "Already Exists");
            }

            if (participation.getStatus() == REJECTED
                    && LocalDateTime.now().isBefore(participation.getDeletedAt().plusDays(3))) {
                throw new GlobalException(NOT_VALID, "Three days have not passed yet.");
            }
        });
    }

    private StudyParticipation findNotAcceptedParticipationByIdAndUserId(Long participationId, UUID userId) {
        StudyParticipation participation = participationRepository.findByIdAndUserId(participationId, userId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study participation not found"));

        if (participation.getStatus() == ACCEPTED) {
            throw new GlobalException(NOT_VALID, "Already accepted study participation");
        }

        return participation;
    }

    private void modifyLinks(ModifyLinkInfo info, StudyParticipation participation) {
        if (info == null) return;

        List<String> linksToAdd = getSafeList(info.getLinksToAdd());
        List<Long> linkIdsToRemove = getSafeList(info.getLinkIdsToRemove());

        List<StudyParticipationLink> links = participationLinkRepository.findAllByParticipationId(participation.getId());

        validateAllIdsPresent(
                linkIdsToRemove,
                links,
                StudyParticipationLink::getId,
                "some links not found"
        );
        validateSizeLimit(
                links.size() + linksToAdd.size() - linkIdsToRemove.size(),
                LINK_LIMIT,
                "links limit exceeded"
        );

        if (isListNotEmpty(linkIdsToRemove))
            participationLinkRepository.deleteAllByIdsAndParticipationId(linkIdsToRemove, participation.getId());

        if (isListNotEmpty(linksToAdd))
            participationLinkRepository.batchInsert(linksToAdd, participation);
    }

    private UpdateStudyParticipationStatusResponse buildUpdateStudyParticipationStatusResponse(
            StudyParticipation participation
    ) {
        if (participation.getStatus() != ACCEPTED) return UpdateStudyParticipationStatusResponse.empty();

        return UpdateStudyParticipationStatusResponse.from(participation);
    }

    private void validateAcceptedCountNotEqualsHeadcountIfAcceptedStatus(
            StudyRecruitmentPosition recruitmentPosition, StudyParticipationStatus status
    ) {
        if (status == ACCEPTED) {
            validateAcceptedCountNotEqualHeadcount(recruitmentPosition);
        }
    }

    private void validateStudyWriter(Study study, UUID userId) {
        if (!study.getUser().getId().equals(userId)) {
            throw new GlobalException(FORBIDDEN, "not study writer");
        }
    }

    private void validateOldStatusMustPending(StudyParticipation participation) {
        if (participation.getStatus() != PENDING) {
            throw new GlobalException(NOT_VALID, "already changed status");
        }
    }

    private void validateNewStatusNotPending(StudyParticipationStatus status) {
        if (status == PENDING) {
            throw new GlobalException(NOT_VALID, "Invalid status value");
        }
    }

    private void insertLinksIfPresent(List<String> links, StudyParticipation participation) {
        if (isListPresent(links)) {
            participationLinkRepository.batchInsert(links, participation);
        }
    }

    private void validateAcceptedCountNotEqualHeadcount(StudyRecruitmentPosition recruitmentPosition) {
        long acceptedCount = participationRepository.countByRecruitmentPositionIdAndAccepted(recruitmentPosition.getId());

        if (recruitmentPosition.getHeadcount() == acceptedCount) {
            throw new GlobalException(NOT_VALID, "Recruitment Position already full");
        }
    }

    private StudyRecruitmentPosition findById(Long recruitmentPositionId) {
        return recruitmentPositionRepository.findById(recruitmentPositionId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "Recruitment Position not found"));
    }
}
