package com.jj.swm.domain.study.core.service;

import com.jj.swm.domain.study.comment.dto.response.FindParentCommentResponse;
import com.jj.swm.domain.study.comment.service.CommentQueryService;
import com.jj.swm.domain.study.core.dto.FindStudyCondition;
import com.jj.swm.domain.study.core.dto.StudyBookmarkInfo;
import com.jj.swm.domain.study.core.dto.response.FindStudyDetailsResponse;
import com.jj.swm.domain.study.core.dto.response.FindStudyImageResponse;
import com.jj.swm.domain.study.core.dto.response.FindStudyResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyImage;
import com.jj.swm.domain.study.core.repository.StudyBookmarkRepository;
import com.jj.swm.domain.study.core.repository.StudyImageRepository;
import com.jj.swm.domain.study.core.repository.StudyLikeRepository;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.recruitmentposition.dto.response.FindRecruitmentPositionResponse;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.constants.PageSize;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyQueryService {

    private final StudyRepository studyRepository;
    private final CommentQueryService commentQueryService;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyImageRepository studyImageRepository;
    private final StudyBookmarkRepository studyBookmarkRepository;
    private final RecruitmentPositionRepository recruitmentPositionRepository;

    @Transactional(readOnly = true)
    public PageResponse<FindStudyResponse> findStudyList(UUID userId, FindStudyCondition condition) {
        List<Study> studyList = studyRepository.findPagedStudyListByCondition(PageSize.Study + 1, condition);

        if (studyList.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = studyList.size() > PageSize.Study;

        List<Study> pagedStudyList = hasNext ? studyList.subList(0, PageSize.Study) : studyList;

        Map<Long, Long> bookmarkIdByStudyId = loadBookmarkMapping(userId, pagedStudyList);

        List<FindStudyResponse> responseList = loadFindStudyResponse(pagedStudyList, bookmarkIdByStudyId);

        return PageResponse.of(responseList, hasNext);
    }

    private List<FindStudyResponse> loadFindStudyResponse(
            List<Study> pagedStudyList, Map<Long, Long> bookmarkIdByStudyId
    ) {
        return pagedStudyList.stream()
                .map(study -> FindStudyResponse.of(
                        study, bookmarkIdByStudyId.getOrDefault(study.getId(), null)
                ))
                .toList();
    }

    @Transactional
    public FindStudyDetailsResponse findStudy(UUID userId, Long studyId) {
        Study study = studyRepository.findByIdWithUserUsingPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        boolean likeStatus = isLike(userId, studyId);

        study.incrementViewCount();

        List<StudyImage> imageList = studyImageRepository.findAllByStudyId(studyId);

        List<FindStudyImageResponse> findImageResponseList = imageList.stream()
                .map(FindStudyImageResponse::from)
                .toList();

        List<StudyRecruitmentPosition> recruitmentPositionList = recruitmentPositionRepository.findAllByStudyId(studyId);

        List<FindRecruitmentPositionResponse> findRecruitmentPositionResponseList = recruitmentPositionList.stream()
                .map(FindRecruitmentPositionResponse::from)
                .toList();

        Pageable pageable = PageRequest.of(
                0,
                PageSize.StudyComment,
                Sort.by("id").descending()
        );
        PageResponse<FindParentCommentResponse> pageCommentResponse =
                commentQueryService.loadPageParentAndReplyCountResponse(studyId, pageable);

        return FindStudyDetailsResponse.of(
                likeStatus,
                study,
                findImageResponseList,
                findRecruitmentPositionResponseList,
                pageCommentResponse
        );
    }

    private boolean isLike(UUID userId, Long studyId) {
        boolean likeStatus = false;

        if (userId != null) {
            likeStatus = studyLikeRepository.existsByUserIdAndStudyId(userId, studyId);
        }
        return likeStatus;
    }

    public Map<Long, Long> loadBookmarkMapping(UUID userId, List<Study> studyList) {
        List<Long> studyIdList = studyList.stream()
                .map(Study::getId)
                .toList();

        return userId != null
                ? studyBookmarkRepository.findAllByUserIdAndStudyIdList(userId, studyIdList)
                .stream()
                .collect(Collectors.toMap(StudyBookmarkInfo::studyId, StudyBookmarkInfo::id))
                : Collections.emptyMap();
    }
}
