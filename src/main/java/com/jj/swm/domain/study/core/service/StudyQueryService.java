package com.jj.swm.domain.study.core.service;

import com.jj.swm.domain.study.comment.dto.response.GetParentStudyCommentResponse;
import com.jj.swm.domain.study.comment.service.StudyCommentQueryService;
import com.jj.swm.domain.study.core.dto.GetStudyCondition;
import com.jj.swm.domain.study.core.dto.StudyBookmarkInfo;
import com.jj.swm.domain.study.core.dto.StudyLikeInfo;
import com.jj.swm.domain.study.core.dto.response.*;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyImage;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
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

import java.util.*;
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

        Map<Long, LikeStatusAndBookmarkId> likeStatusAndBookmarkIdByStudyId =
                getLikeStatusAndBookmarkIdByStudyIdBasedOnLogin(pagedStudy, userId);

        List<StudyParticipation> participations = new ArrayList<>();
        if (userId != null)
            participations = participationRepository.findByStudyIdsAndUserIdWithRecruitmentPosition(pagedStudy.stream().map(Study::getId).toList(), userId);

        Map<Long, GetStudyParticipationStatusResponse> map = new HashMap<>();
        for (StudyParticipation participation : participations) {
            map.put(participation.getStudy().getId(), GetStudyParticipationStatusResponse.from(participation));
        }

        List<GetStudyResponse> responses = pagedStudy.stream()
                .map(study -> GetStudyResponse.of(
                        study,
                        likeStatusAndBookmarkIdByStudyId.get(study.getId()).bookmarkId,
                        likeStatusAndBookmarkIdByStudyId.get(study.getId()).likeStatus,
                        map.get(study.getId())
                )).toList();

        return PageResponse.of(responses, hasNext);
    }

    @Transactional
    public GetStudyDetailsResponse getStudyDetails(Long studyId, UUID userId) {
        Study study = studyRepository.findByIdWithUserUsingLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        LikeStatusAndBookmarkId likeStatusAndBookmarkId = getLikeStatusAndBookmarkIdBasedOnLogin(studyId, userId);

        study.incrementViewCount();

        List<StudyImage> images = studyImageRepository.findAllByStudyId(studyId);

        List<GetStudyImageResponse> getImageResponses = images.stream()
                .map(GetStudyImageResponse::from)
                .toList();

        List<StudyRecruitmentPosition> recruitmentPositions = study.getStudyRecruitmentPositions();

        List<Long> recruitmentPositionIds = recruitmentPositions.stream()
                .map(StudyRecruitmentPosition::getId)
                .toList();

        Map<Long, StudyParticipationCountInfo> studyParticipationCountByRecruitmentId =
                participationRepository.countByRecruitmentPositionIds(recruitmentPositionIds).stream()
                        .collect(Collectors.toMap(
                                StudyParticipationCountInfo::getRecruitmentPositionId,
                                info -> info
                        ));

        List<GetRecruitmentPositionDetailsResponse> getRecruitmentPositionDetailsResponses =
                recruitmentPositions.stream().map(recruitmentPosition -> GetRecruitmentPositionDetailsResponse.of(
                        recruitmentPosition,
                        studyParticipationCountByRecruitmentId.get(recruitmentPosition.getId())==null ? 0: studyParticipationCountByRecruitmentId.get(recruitmentPosition.getId()).getAcceptedCount(),
                        studyParticipationCountByRecruitmentId.get(recruitmentPosition.getId())==null ? 0: studyParticipationCountByRecruitmentId.get(recruitmentPosition.getId()).getTotalCount()
                )).toList();

        Pageable pageable = PageRequest.of(
                0,
                PageSize.StudyComment,
                Sort.by("id").descending()
        );

        PageResponse<GetParentStudyCommentResponse> pageCommentResponse =
                commentQueryService.getPageParentAndReplyCountResponse(studyId, pageable);

        Optional<StudyParticipation> optionalParticipation = Optional.empty();
        if (userId != null)
            optionalParticipation = participationRepository.findByStudyIdAndUserIdWithRecruitmentPosition(studyId, userId);

        GetStudyParticipationStatusResponse getStudyParticipationStatusResponse = null;
        if (optionalParticipation.isPresent()) {
            getStudyParticipationStatusResponse = GetStudyParticipationStatusResponse.from(optionalParticipation.get());
        }

        return GetStudyDetailsResponse.of(
                study,
                likeStatusAndBookmarkId.likeStatus(),
                likeStatusAndBookmarkId.bookmarkId(),
                getRecruitmentPositionDetailsResponses,
                getImageResponses,
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

    private Map<Long, LikeStatusAndBookmarkId> getLikeStatusAndBookmarkIdByStudyIdBasedOnLogin(
            List<Study> studies, UUID userId
    ) {
        if (userId == null) {
            return studies.stream()
                    .collect(Collectors.toMap(Study::getId, study -> new LikeStatusAndBookmarkId(false, null)));
        }

        List<Long> studyIds = studies.stream()
                .map(Study::getId)
                .toList();

        Map<Long, Long> likeIdByStudyId = studyLikeRepository.findAllByUserIdAndStudyIds(studyIds, userId).stream()
                .collect(Collectors.toMap(StudyLikeInfo::studyId, StudyLikeInfo::id));

        Map<Long, Long> bookmarkIdByStudyId =
                studyBookmarkRepository.findAllByUserIdAndStudyIds(userId, studyIds).stream()
                        .collect(Collectors.toMap(StudyBookmarkInfo::studyId, StudyBookmarkInfo::id));

        return studyIds.stream()
                .collect(Collectors.toMap(studyId -> studyId, studyId -> new LikeStatusAndBookmarkId(
                        likeIdByStudyId.getOrDefault(studyId, null) != null,
                        bookmarkIdByStudyId.getOrDefault(studyId, null)
                )));
    }

    private LikeStatusAndBookmarkId getLikeStatusAndBookmarkIdBasedOnLogin(Long studyId, UUID userId) {
        if (userId == null) {
            return new LikeStatusAndBookmarkId(false, null);
        }

        return new LikeStatusAndBookmarkId(
                studyLikeRepository.existsByStudyIdAndUserId(studyId, userId),
                studyBookmarkRepository.findIdByStudyIdAndUserId(studyId, userId)
        );
    }

    private record LikeStatusAndBookmarkId(boolean likeStatus, Long bookmarkId) {
    }
}
