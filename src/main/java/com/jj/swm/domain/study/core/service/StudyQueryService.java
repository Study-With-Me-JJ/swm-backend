package com.jj.swm.domain.study.core.service;

import com.jj.swm.domain.study.comment.dto.response.GetStudyCommentResponse;
import com.jj.swm.domain.study.comment.service.StudyCommentQueryService;
import com.jj.swm.domain.study.core.dto.GetStudyCondition;
import com.jj.swm.domain.study.core.dto.UserInteractionInfo;
import com.jj.swm.domain.study.core.dto.response.*;
import com.jj.swm.domain.study.core.entity.*;
import com.jj.swm.domain.study.core.repository.StudyBookmarkRepository;
import com.jj.swm.domain.study.core.repository.StudyImageRepository;
import com.jj.swm.domain.study.core.repository.StudyLikeRepository;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.participation.dto.StudyParticipationCountInfo;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.global.common.constants.PageSize;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyQueryService {

    private final StudyRepository studyRepository;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyImageRepository studyImageRepository;
    private final StudyCommentQueryService commentQueryService;
    private final StudyBookmarkRepository studyBookmarkRepository;
    private final StudyParticipationRepository participationRepository;

    @Transactional(readOnly = true)
    public PageResponse<GetStudyResponse> getStudies(UUID userId, GetStudyCondition condition) {
        List<Study> studies = studyRepository.findPagedStudyByCondition(condition, PageSize.Study + 1);

        if (studies.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = studies.size() > PageSize.Study;

        List<Study> pagedStudy = hasNext ? studies.subList(0, PageSize.Study) : studies;

        Map<Long, UserInteractionInfo> userInteractionInfoByStudyId = getUserInteractionInfoByStudyIdBasedOnLogin(
                pagedStudy, userId
        );

        Map<Long, GetStudyParticipationStatusResponse> getStudyParticipationStatusResponseByStudyId =
                getGetStudyParticipationStatusResponseByStudyIdBasedOnLogin(pagedStudy, userId);

        List<GetStudyResponse> responses = pagedStudy.stream()
                .map(study -> GetStudyResponse.of(
                        study,
                        userInteractionInfoByStudyId.get(study.getId()),
                        getStudyParticipationStatusResponseByStudyId.get(study.getId())
                )).toList();

        return PageResponse.of(responses, hasNext);
    }

    @Transactional
    public GetStudyDetailsResponse getStudyDetails(Long studyId, UUID userId) {
        Study study = studyRepository.findByIdWithUserUsingLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        UserInteractionInfo userInteractionInfo = getUserInteractionInfoBasedOnLogin(studyId, userId);

        study.incrementViewCount();

        List<GetStudyImageResponse> getStudyImageResponses = getGetStudyImageResponses(studyId);

        List<StudyRecruitmentPosition> recruitmentPositions = study.getStudyRecruitmentPositions();

        List<GetRecruitmentPositionDetailsResponse> getRecruitmentPositionDetailsResponses =
                getGetRecruitmentPositionDetailsResponses(recruitmentPositions);

        PageResponse<GetStudyCommentResponse> pageCommentResponse = getGetStudyCommentResponsePageResponse(studyId);

        GetStudyParticipationStatusResponse getStudyParticipationStatusResponse = getGetStudyParticipationStatusResponse(studyId, userId);

        return GetStudyDetailsResponse.of(
                study,
                userInteractionInfo,
                getRecruitmentPositionDetailsResponses,
                getStudyImageResponses,
                pageCommentResponse,
                getStudyParticipationStatusResponse
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyResponse> getUserLikedStudies(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<Study> pagedStudy = studyLikeRepository.findPagedStudyByUserId(userId, pageable);

        return PageResponse.of(pagedStudy, GetStudyResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyResponse> getUserBookmarkedStudies(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<Study> pagedStudy = studyBookmarkRepository.findPagedStudyByUserId(userId, pageable);

        return PageResponse.of(pagedStudy, GetStudyResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyResponse> getUserStudies(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<Study> pagedStudy = studyRepository.findAllByUserId(userId, pageable);

        return PageResponse.of(pagedStudy, GetStudyResponse::from);
    }

    private Map<Long, UserInteractionInfo> getUserInteractionInfoByStudyIdBasedOnLogin(
            List<Study> studies, UUID userId
    ) {
        if (userId == null) {
            return studies.stream()
                    .collect(Collectors.toMap(Study::getId, study -> null));
        }

        List<Long> studyIds = studies.stream()
                .map(Study::getId)
                .toList();

        Map<Long, Long> likeIdByStudyId = studyLikeRepository.findAllByStudyIdInAndUserId(studyIds, userId).stream()
                .collect(Collectors.toMap(like -> like.getStudy().getId(), StudyLike::getId));

        Map<Long, Long> bookmarkIdByStudyId =
                studyBookmarkRepository.findAllByStudyIdInAndUserId(studyIds, userId).stream()
                        .collect(Collectors.toMap(bookmark -> bookmark.getStudy().getId(), StudyBookmark::getId));

        return studyIds.stream()
                .collect(Collectors.toMap(studyId -> studyId, studyId -> new UserInteractionInfo(
                        bookmarkIdByStudyId.getOrDefault(studyId, null),
                        likeIdByStudyId.getOrDefault(studyId, null) != null
                )));
    }

    private PageResponse<GetStudyCommentResponse> getGetStudyCommentResponsePageResponse(Long studyId) {
        Pageable pageable = PageRequest.of(
                0,
                PageSize.StudyComment,
                Sort.by("id").descending()
        );

        return commentQueryService.buildStudyCommentPageResponse(studyId, pageable);
    }

    private GetStudyParticipationStatusResponse getGetStudyParticipationStatusResponse(Long studyId, UUID userId) {
        if (userId == null)
            return null;

        Optional<StudyParticipation> optionalParticipation =
                participationRepository.findByStudyIdAndUserIdWithRecruitmentPosition(studyId, userId);

        GetStudyParticipationStatusResponse getStudyParticipationStatusResponse = null;
        if (optionalParticipation.isPresent()) {
            getStudyParticipationStatusResponse = GetStudyParticipationStatusResponse.from(optionalParticipation.get());
        }

        return getStudyParticipationStatusResponse;
    }

    private List<GetRecruitmentPositionDetailsResponse> getGetRecruitmentPositionDetailsResponses(List<StudyRecruitmentPosition> recruitmentPositions) {
        List<Long> recruitmentPositionIds = recruitmentPositions.stream()
                .map(StudyRecruitmentPosition::getId)
                .toList();

        Map<Long, StudyParticipationCountInfo> studyParticipationCountByRecruitmentId =
                participationRepository.countByRecruitmentPositionIds(recruitmentPositionIds).stream()
                        .collect(Collectors.toMap(
                                StudyParticipationCountInfo::getRecruitmentPositionId,
                                info -> info
                        ));

        return recruitmentPositions.stream().map(recruitmentPosition -> GetRecruitmentPositionDetailsResponse.of(
                recruitmentPosition,
                studyParticipationCountByRecruitmentId.get(recruitmentPosition.getId()) == null ?
                        0L : studyParticipationCountByRecruitmentId.get(recruitmentPosition.getId()).getAcceptedCount(),
                studyParticipationCountByRecruitmentId.get(recruitmentPosition.getId()) == null ?
                        0L : studyParticipationCountByRecruitmentId.get(recruitmentPosition.getId()).getTotalCount()
        )).toList();
    }

    private List<GetStudyImageResponse> getGetStudyImageResponses(Long studyId) {
        List<StudyImage> images = studyImageRepository.findAllByStudyId(studyId);

        return images.stream()
                .map(GetStudyImageResponse::from)
                .toList();
    }

    private Map<Long, GetStudyParticipationStatusResponse> getGetStudyParticipationStatusResponseByStudyIdBasedOnLogin(
            List<Study> studies, UUID userId
    ) {
        if (userId == null) {
            return studies.stream()
                    .collect(Collectors.toMap(Study::getId, study -> null));
        }
        List<StudyParticipation> participations = participationRepository.findByStudyIdsAndUserIdWithRecruitmentPosition(
                studies.stream().map(Study::getId).toList(), userId
        );

        return participations.stream()
                .collect(Collectors.toMap(participation -> participation.getStudy().getId(), GetStudyParticipationStatusResponse::from));
    }

    private UserInteractionInfo getUserInteractionInfoBasedOnLogin(Long studyId, UUID userId) {
        if (userId == null) {
            return null;
        }

        return new UserInteractionInfo(
                studyBookmarkRepository.findIdByStudyIdAndUserId(studyId, userId),
                studyLikeRepository.existsByStudyIdAndUserId(studyId, userId)
        );
    }
}
