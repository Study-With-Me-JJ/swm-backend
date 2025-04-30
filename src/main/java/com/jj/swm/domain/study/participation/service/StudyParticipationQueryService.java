package com.jj.swm.domain.study.participation.service;

import com.jj.swm.domain.study.core.dto.response.GetStudyResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.participation.dto.GetStudyParticipationCondition;
import com.jj.swm.domain.study.participation.dto.response.GetStudyParticipationDetailsResponse;
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

    private final RecruitmentPositionRepository recruitmentPositionRepository;
    private final StudyParticipationRepository participationRepository;
    private final StudyParticipationLinkRepository participationLinkRepository;
    private final StudyRepository studyRepository;

    @Transactional(readOnly = true)
    public PageResponse<GetStudyParticipationResponse> getStudyParticipations(
            Long recruitmentPositionId,
            UUID userId,
            GetStudyParticipationCondition condition
    ) {
        StudyRecruitmentPosition recruitmentPosition =
                recruitmentPositionRepository.findByIdWithStudy(recruitmentPositionId)
                        .orElseThrow(() -> new GlobalException(NOT_FOUND, "recruitment position not found"));

        validateStudyWriter(recruitmentPosition, userId);

        Pageable pageable = PageRequest.of(condition.getPageNo(), PageSize.StudyParticipation);

        Page<StudyParticipation> participations =
                participationRepository.findPagedStudyParticipationByStatusWithUser(
                        recruitmentPositionId,
                        condition.getStatus(),
                        pageable
                );

        return PageResponse.of(participations, GetStudyParticipationResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyParticipationInMyPageResponse> getStudyParticipationsInMyPage(
            Long studyId,
            UUID userId,
            GetStudyParticipationCondition condition
    ) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study not found"));

        if(!study.getUser().getId().equals(userId)){
            throw new GlobalException(FORBIDDEN, "not study writer");
        }

        Pageable pageable = PageRequest.of(condition.getPageNo(), PageSize.StudyParticipation);

        Page<StudyParticipation> participations =
                participationRepository.findPagedStudyParticipationByStatusWithUserInMyPage(
                        studyId,
                        condition.getStatus(),
                        pageable
                );

        return PageResponse.of(participations, GetStudyParticipationInMyPageResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyResponse> getUserParticipantStudy(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<Study> studies = participationRepository.findPagedStudyByUserId(userId, pageable);

        return PageResponse.of(studies, GetStudyResponse::from);
    }

    @Transactional(readOnly = true)
    public GetStudyParticipationDetailsResponse getStudyParticipationDetails(
            Long participationId, UUID userId
    ) {
        StudyParticipation participation =
                participationRepository.findByIdWithUserAndStudy(participationId)
                        .orElseThrow(() -> new GlobalException(NOT_FOUND, "study participation not found"));

        boolean isStudyWriter;
        if (participation.getUser().getId().equals(userId)) {
            isStudyWriter = false;
        } else if (participation.getStudy().getUser().getId().equals(userId)) {
            isStudyWriter = true;
        } else {
            throw new GlobalException(FORBIDDEN, "No Authorization");
        }

        List<StudyParticipationLink> participationLinks =
                participationLinkRepository.findAllByParticipationId(participationId);

        return GetStudyParticipationDetailsResponse.of(
                participation,
                participationLinks,
                isStudyWriter
        );
    }

    private void validateStudyWriter(StudyRecruitmentPosition recruitmentPosition, UUID userId) {
        if (!recruitmentPosition.getStudy().getUser().getId().equals(userId)) {
            throw new GlobalException(FORBIDDEN, "not study writer");
        }
    }
}
