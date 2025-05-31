package com.jj.swm.domain.study.participation.service;

import com.jj.swm.domain.study.core.dto.response.GetStudyResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.participation.dto.request.GetStudyParticipationCondition;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse.LinkInfo;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationInMyPageResponse;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationResponse;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipationLink;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.global.common.constants.PageSize;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.jj.swm.global.common.enums.ErrorCode.FORBIDDEN;
import static com.jj.swm.global.common.enums.ErrorCode.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StudyParticipationQueryService {

    private final StudyRepository studyRepository;
    private final StudyParticipationRepository participationRepository;
    private final RecruitmentPositionRepository recruitmentPositionRepository;
    private final StudyParticipationLinkRepository participationLinkRepository;

    @Transactional(readOnly = true)
    public PageResponse<GetStudyParticipationResponse> getStudyParticipations(
            Long recruitmentPositionId,
            UUID userId,
            GetStudyParticipationCondition condition
    ) {
        validateStudyWriterByRecruitmentPositionId(recruitmentPositionId, userId);

        Page<StudyParticipation> PagedParticipation = participationRepository.findPagedParticipationByStatusWithUser(
                recruitmentPositionId,
                condition.getStatus(),
                PageRequest.of(condition.getPageNo(), PageSize.StudyParticipation)
        );

        return PageResponse.of(PagedParticipation, GetStudyParticipationResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyParticipationInMyPageResponse> getStudyParticipationsInMyPage(
            Long studyId,
            UUID userId,
            GetStudyParticipationCondition condition
    ) {
        validateStudyWriterByStudyId(studyId, userId);

        Page<StudyParticipation> PagedParticipation =
                participationRepository.findPagedParticipationByStatusWithUserInMyPage(
                        studyId,
                        condition.getStatus(),
                        PageRequest.of(condition.getPageNo(), PageSize.StudyParticipation)
                );

        return PageResponse.of(PagedParticipation, GetStudyParticipationInMyPageResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyResponse> getUserParticipatedStudies(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<Study> pagedStudy = participationRepository.findPagedStudyByUserId(userId, pageable);

        return PageResponse.of(pagedStudy, GetStudyResponse::from);
    }

    @Transactional(readOnly = true)
    public GetStudyParticipationDetailsResponse getStudyParticipationDetails(
            Long participationId, UUID userId
    ) {
        StudyParticipation participation = participationRepository.findByIdWithUserAndStudy(participationId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study participation not found"));

        boolean isStudyWriter = isStudyWriter(participation, userId);

        List<LinkInfo> linkInfos = getLinkInfos(participationId);

        return GetStudyParticipationDetailsResponse.of(
                participation,
                linkInfos,
                isStudyWriter
        );
    }

    private boolean isStudyWriter(StudyParticipation participation, UUID userId) {
        boolean isStudyWriter;

        if (participation.getStudy().getUser().getId().equals(userId)) {
            isStudyWriter = true;
        } else if (participation.getUser().getId().equals(userId)) {
            isStudyWriter = false;
        } else {
            throw new GlobalException(FORBIDDEN, "No Authorization");
        }

        return isStudyWriter;
    }

    private void validateStudyWriter(Study study, UUID userId) {
        if (!study.getUser().getId().equals(userId)) {
            throw new GlobalException(FORBIDDEN, "not study writer");
        }
    }

    private void validateStudyWriterByStudyId(Long studyId, UUID userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study not found"));

        validateStudyWriter(study, userId);
    }

    private void validateStudyWriterByRecruitmentPositionId(Long recruitmentPositionId, UUID userId) {
        StudyRecruitmentPosition recruitmentPosition =
                recruitmentPositionRepository.findByIdWithStudy(recruitmentPositionId)
                        .orElseThrow(() -> new GlobalException(NOT_FOUND, "recruitment position not found"));

        validateStudyWriter(recruitmentPosition.getStudy(), userId);
    }

    private List<LinkInfo> getLinkInfos(Long participationId) {
        List<StudyParticipationLink> participationLinks =
                participationLinkRepository.findAllByParticipationId(participationId);

        return participationLinks.stream()
                .map(LinkInfo::from)
                .toList();
    }
}
