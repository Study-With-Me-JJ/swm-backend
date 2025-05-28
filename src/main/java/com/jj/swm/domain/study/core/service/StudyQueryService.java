package com.jj.swm.domain.study.core.service;

import com.jj.swm.domain.study.comment.dto.response.GetStudyParentCommentResponse;
import com.jj.swm.domain.study.comment.service.StudyCommentQueryService;
import com.jj.swm.domain.study.core.dto.component.ParticipationStatusInfo;
import com.jj.swm.domain.study.core.dto.component.UserInteractionInfo;
import com.jj.swm.domain.study.core.dto.request.GetStudyCondition;
import com.jj.swm.domain.study.core.dto.response.GetStudyDetailsResponse;
import com.jj.swm.domain.study.core.dto.response.GetStudyDetailsResponse.ImageInfo;
import com.jj.swm.domain.study.core.dto.response.GetStudyDetailsResponse.RecruitmentPositionDetailsInfo;
import com.jj.swm.domain.study.core.dto.response.GetStudyDetailsResponse.RecruitmentPositionDetailsInfo.RecruitmentPositionStat;
import com.jj.swm.domain.study.core.dto.response.GetStudyResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyImage;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.repository.StudyBookmarkRepository;
import com.jj.swm.domain.study.core.repository.StudyImageRepository;
import com.jj.swm.domain.study.core.repository.StudyLikeRepository;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.domain.study.participation.repository.dto.StudyParticipationCountInfo;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jj.swm.global.common.enums.ErrorCode.NOT_FOUND;

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

        Map<Long, UserInteractionInfo> userInteractionInfoByStudyId = getUserInteractionInfoByStudyId(pagedStudy, userId);

        Map<Long, ParticipationStatusInfo> participationStatusInfoByStudyId =
                getParticipationStatusInfoByStudyId(pagedStudy, userId);

        List<GetStudyResponse> responses = pagedStudy.stream()
                .map(study -> GetStudyResponse.of(
                        study,
                        userInteractionInfoByStudyId.get(study.getId()),
                        participationStatusInfoByStudyId.get(study.getId())
                )).toList();

        return PageResponse.of(responses, hasNext);
    }

    @Transactional
    public GetStudyDetailsResponse getStudyDetails(Long studyId, UUID userId) {
        Study study = studyRepository.findByIdWithUserAndRecruitmentPosition(studyId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study not found"));

        UserInteractionInfo userInteractionInfo = getUserInteractionInfo(studyId, userId);

        List<ImageInfo> imageInfos = getImageInfos(studyId);

        List<RecruitmentPositionDetailsInfo> recruitmentPositionDetailsInfos =
                getRecruitmentPositionDetailsInfo(study.getStudyRecruitmentPositions());

        PageResponse<GetStudyParentCommentResponse> pageComment = getGetStudyCommentResponsePageResponse(studyId);

        ParticipationStatusInfo participationStatusInfo = getParticipationStatusInfo(studyId, userId);

        studyRepository.incrementViewCountById(studyId);

        return GetStudyDetailsResponse.of(
                study,
                userInteractionInfo,
                recruitmentPositionDetailsInfos,
                imageInfos,
                pageComment,
                participationStatusInfo
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

    private Map<Long, UserInteractionInfo> getUserInteractionInfoByStudyId(List<Study> studies, UUID userId) {
        Map<Long, UserInteractionInfo> userInteractionInfoByStudyId = studies.stream()
                .collect(Collectors.toMap(Study::getId, study -> UserInteractionInfo.empty()));

        if (userId == null) return userInteractionInfoByStudyId;

        studyBookmarkRepository.findAllByStudyIdInAndUserId(userInteractionInfoByStudyId.keySet(), userId)
                .forEach(bookmark -> userInteractionInfoByStudyId.computeIfPresent(
                        bookmark.getStudy().getId(),
                        (id, info) -> UserInteractionInfo.of(bookmark.getId(), info.getLikeId())
                ));

        studyLikeRepository.findAllByStudyIdInAndUserId(userInteractionInfoByStudyId.keySet(), userId)
                .forEach(like -> userInteractionInfoByStudyId.computeIfPresent(
                        like.getStudy().getId(), (id, info) -> UserInteractionInfo.of(info.getBookmarkId(), like.getId())
                ));

        return userInteractionInfoByStudyId;
    }

    private PageResponse<GetStudyParentCommentResponse> getGetStudyCommentResponsePageResponse(Long studyId) {
        Pageable pageable = PageRequest.of(
                0,
                PageSize.StudyParentComment,
                Sort.by("id").descending()
        );

        return commentQueryService.buildParentCommentPageResponse(studyId, pageable);
    }

    private ParticipationStatusInfo getParticipationStatusInfo(Long studyId, UUID userId) {
        if (userId == null) return ParticipationStatusInfo.empty();

        return participationRepository.findByStudyIdAndUserId(studyId, userId)
                .map(ParticipationStatusInfo::from)
                .orElse(ParticipationStatusInfo.empty());
    }

    private List<RecruitmentPositionDetailsInfo> getRecruitmentPositionDetailsInfo(
            List<StudyRecruitmentPosition> recruitmentPositions
    ) {
        List<Long> recruitmentPositionIds = recruitmentPositions.stream()
                .map(StudyRecruitmentPosition::getId)
                .toList();

        Map<Long, RecruitmentPositionStat> positionStatByPositionId =
                participationRepository.countByRecruitmentPositionIds(recruitmentPositionIds).stream()
                        .collect(Collectors.toMap(
                                StudyParticipationCountInfo::getRecruitmentPositionId, RecruitmentPositionStat::from
                        ));

        return recruitmentPositions.stream()
                .map(recruitmentPosition -> RecruitmentPositionDetailsInfo.of(
                        recruitmentPosition,
                        positionStatByPositionId.getOrDefault(recruitmentPosition.getId(), RecruitmentPositionStat.empty())
                )).toList();
    }

    private List<ImageInfo> getImageInfos(Long studyId) {
        List<StudyImage> images = studyImageRepository.findAllByStudyId(studyId);

        return images.stream()
                .map(ImageInfo::from)
                .toList();
    }

    private Map<Long, ParticipationStatusInfo> getParticipationStatusInfoByStudyId(List<Study> studies, UUID userId) {
        Map<Long, ParticipationStatusInfo> participationStatusInfoByStudyId = studies.stream()
                .collect(Collectors.toMap(Study::getId, study -> ParticipationStatusInfo.empty()));

        if (userId == null) return participationStatusInfoByStudyId;

        participationRepository.findAllByStudyIdsAndUserIdWithPosition(participationStatusInfoByStudyId.keySet(), userId)
                .forEach(participation -> participationStatusInfoByStudyId.put(
                        participation.getStudy().getId(), ParticipationStatusInfo.from(participation))
                );

        return participationStatusInfoByStudyId;
    }

    private UserInteractionInfo getUserInteractionInfo(Long studyId, UUID userId) {
        if (userId == null) return UserInteractionInfo.empty();

        return UserInteractionInfo.of(
                studyBookmarkRepository.findIdByStudyIdAndUserId(studyId, userId),
                studyLikeRepository.findIdByStudyIdAndUserId(studyId, userId)
        );
    }
}
