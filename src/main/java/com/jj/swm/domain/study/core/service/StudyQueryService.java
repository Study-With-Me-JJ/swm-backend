package com.jj.swm.domain.study.core.service;

import com.jj.swm.domain.study.comment.dto.response.GetParentCommentResponse;
import com.jj.swm.domain.study.comment.service.CommentQueryService;
import com.jj.swm.domain.study.core.dto.GetStudyCondition;
import com.jj.swm.domain.study.core.dto.StudyBookmarkInfo;
import com.jj.swm.domain.study.core.dto.StudyLikeInfo;
import com.jj.swm.domain.study.core.dto.response.GetStudyDetailsResponse;
import com.jj.swm.domain.study.core.dto.response.GetStudyImageResponse;
import com.jj.swm.domain.study.core.dto.response.GetStudyResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyImage;
import com.jj.swm.domain.study.core.repository.StudyBookmarkRepository;
import com.jj.swm.domain.study.core.repository.StudyImageRepository;
import com.jj.swm.domain.study.core.repository.StudyLikeRepository;
import com.jj.swm.domain.study.core.repository.StudyRepository;
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

    @Transactional(readOnly = true)
    public PageResponse<GetStudyResponse> findStudyList(UUID userId, GetStudyCondition condition) {
        List<Study> studyList = studyRepository.findPagedStudyListByCondition(PageSize.Study + 1, condition);

        if (studyList.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = studyList.size() > PageSize.Study;

        List<Study> pagedStudyList = hasNext ? studyList.subList(0, PageSize.Study) : studyList;

        Map<Long, LikeStatusAndBookmarkId> likeStatusAndBookmarkIdByStudyId =
                loadLikeStatusAndBookmarkByStudyIdIdBasedOnLogin(userId, pagedStudyList);

        List<GetStudyResponse> responseList = loadGetStudyResponse(pagedStudyList, likeStatusAndBookmarkIdByStudyId);

        return PageResponse.of(responseList, hasNext);
    }

    private Map<Long, LikeStatusAndBookmarkId> loadLikeStatusAndBookmarkByStudyIdIdBasedOnLogin(
            UUID userId, List<Study> studyList
    ) {
        if (userId == null) {
            return studyList.stream()
                    .collect(Collectors.toMap(Study::getId, study -> new LikeStatusAndBookmarkId(false, null)));
        }

        List<Long> studyIdList = studyList.stream()
                .map(Study::getId)
                .toList();

        Map<Long, Long> collect = studyLikeRepository.findAllByUserIdAndStudyIdList(userId, studyIdList).stream()
                .collect(Collectors.toMap(StudyLikeInfo::studyId, StudyLikeInfo::id));

        Map<Long, Long> collect1 = studyBookmarkRepository.findAllByUserIdAndStudyIdList(userId, studyIdList).stream()
                .collect(Collectors.toMap(StudyBookmarkInfo::studyId, StudyBookmarkInfo::id));

        return studyIdList.stream()
                .collect(Collectors.toMap(studyId -> studyId, studyId -> new LikeStatusAndBookmarkId(
                        collect.getOrDefault(studyId, null) != null, collect1.getOrDefault(studyId, null)
                )));
    }

    private List<GetStudyResponse> loadGetStudyResponse(
            List<Study> pagedStudyList, Map<Long, LikeStatusAndBookmarkId> likeStatusAndBookmarkIdByStudyId
    ) {
        return pagedStudyList.stream()
                .map(study -> GetStudyResponse.of(
                        study,
                        likeStatusAndBookmarkIdByStudyId.get(study.getId()).bookmarkId,
                        likeStatusAndBookmarkIdByStudyId.get(study.getId()).likeStatus
                )).toList();
    }

    @Transactional
    public GetStudyDetailsResponse findStudy(UUID userId, Long studyId) {
        Study study = studyRepository.findByIdWithUserUsingPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        LikeStatusAndBookmarkId likeStatusAndBookmarkId = loadLikeStatusAndBookmarkIdBasedOnLogin(userId, studyId);

        study.incrementViewCount();

        List<StudyImage> imageList = studyImageRepository.findAllByStudyId(studyId);

        List<GetStudyImageResponse> getImageResponseList = imageList.stream()
                .map(GetStudyImageResponse::from)
                .toList();

        Pageable pageable = PageRequest.of(
                0,
                PageSize.StudyComment,
                Sort.by("id").descending()
        );
        PageResponse<GetParentCommentResponse> pageCommentResponse =
                commentQueryService.loadPageParentAndReplyCountResponse(studyId, pageable);

        return GetStudyDetailsResponse.of(
                study,
                likeStatusAndBookmarkId.likeStatus(),
                likeStatusAndBookmarkId.bookmarkId(),
                getImageResponseList,
                pageCommentResponse
        );
    }

    private LikeStatusAndBookmarkId loadLikeStatusAndBookmarkIdBasedOnLogin(UUID userId, Long studyId) {
        if (userId == null) {
            return new LikeStatusAndBookmarkId(false, null);
        }

        return new LikeStatusAndBookmarkId(
                studyLikeRepository.existsByUserIdAndStudyId(userId, studyId),
                studyBookmarkRepository.findIdByUserIdAndStudyId(userId, studyId)
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyResponse> findUserLikedStudyList(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<Study> pagedStudy = studyLikeRepository.findPagedStudyByUserId(userId, pageable);

        return PageResponse.of(pagedStudy, GetStudyResponse::of);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyResponse> findUserBookmarkedStudyList(UUID userId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.Study,
                Sort.by("id").descending()
        );

        Page<Study> pagedStudy = studyBookmarkRepository.findPagedStudyByUserId(userId, pageable);

        return PageResponse.of(pagedStudy, GetStudyResponse::of);
    }

    private record LikeStatusAndBookmarkId(boolean likeStatus, Long bookmarkId) {
    }
}
