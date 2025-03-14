package com.jj.swm.domain.studyroom.core.service;

import com.jj.swm.domain.studyroom.core.dto.StudyRoomLikeInfo;
import com.jj.swm.domain.studyroom.core.dto.response.*;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.repository.*;
import com.jj.swm.domain.studyroom.core.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.core.dto.StudyRoomBookmarkInfo;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.constants.PageSize;
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
public class StudyRoomQueryService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomBookmarkRepository bookmarkRepository;
    private final StudyRoomLikeRepository likeRepository;
    private final StudyRoomOptionInfoRepository optionInfoRepository;
    private final StudyRoomDayOffRepository dayOffRepository;
    private final StudyRoomReserveTypeRepository reserveTypeRepository;
    private final StudyRoomImageRepository imageRepository;
    private final StudyRoomTypeInfoRepository typeReInfoRepository;

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomResponse> getStudyRooms(
            GetStudyRoomCondition condition,
            UUID userId
    ) {
        List<StudyRoom> studyRooms
                = studyRoomRepository.findPagedStudyRoomByCondition(PageSize.StudyRoom + 1, condition);

        if(studyRooms.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = studyRooms.size() > PageSize.StudyRoom;

        List<StudyRoom> pagedStudyRooms = hasNext ? studyRooms.subList(0, PageSize.StudyRoom) : studyRooms;

        Map<Long, LikeStatusAndBookmarkId> likeStatusAndBookmarkIdMap
                = getStudyRoomLikeStatusAndBookmarkIdMap(pagedStudyRooms, userId);

        List<GetStudyRoomResponse> responses = pagedStudyRooms.stream()
                .map(studyRoom -> GetStudyRoomResponse.of(
                        studyRoom,
                        likeStatusAndBookmarkIdMap.get(studyRoom.getId()).likeStatus,
                        likeStatusAndBookmarkIdMap.get(studyRoom.getId()).bookmarkId()
                )).toList();

        return PageResponse.of(responses, hasNext);
    }

    @Transactional(readOnly = true)
    public GetStudyRoomDetailResponse getStudyRoomDetails(Long studyRoomId, UUID userId) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithTags(studyRoomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoom Not Found"));

        LikeStatusAndBookmarkId likeStatusAndBookmarkId
                = getLikeStatusAndBookmarkIdByStudyRoomIdAndUserId(studyRoomId, userId);

        return buildGetStudyRoomDetailsResponse(
                likeStatusAndBookmarkId.likeStatus,
                likeStatusAndBookmarkId.bookmarkId,
                studyRoom
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomResponse> getUserStudyRooms(int pageNo, UUID userId) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyRoom,
                Sort.by("id").descending()
        );

        Page<StudyRoom> pagedStudyRoom
                = studyRoomRepository.findPagedStudyRoomByUserId(userId, pageable);

        return PageResponse.of(pagedStudyRoom, GetStudyRoomResponse::of);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomResponse> getUserLikedStudyRooms(int pageNo, UUID userId) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyRoom,
                Sort.by("id").descending()
        );

        Page<StudyRoom> pagedStudyRoom
                = likeRepository.findPagedStudyRoomByUserId(userId, pageable);

        return PageResponse.of(pagedStudyRoom, GetStudyRoomResponse::of);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomResponse> getUserBookmarkedStudyRooms(int pageNo, UUID userId) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyRoom,
                Sort.by("id").descending()
        );

        Page<StudyRoom> pagedStudyRoom
                = bookmarkRepository.findPagedStudyRoomByUserId(userId, pageable);

        return PageResponse.of(pagedStudyRoom, GetStudyRoomResponse::of);
    }

    private GetStudyRoomDetailResponse buildGetStudyRoomDetailsResponse(
            boolean likeStatus,
            Long bookmarkId,
            StudyRoom studyRoom
    ) {
        List<GetStudyRoomImageResponse> imageResponses = imageRepository.findAllByStudyRoomId(studyRoom.getId()).stream()
                .map(GetStudyRoomImageResponse::from)
                .toList();;

        List<GetStudyRoomDayOffResponse> dayOffResponses = dayOffRepository.findAllByStudyRoomId(studyRoom.getId()).stream()
                .map(GetStudyRoomDayOffResponse::from)
                .toList();;

        List<GetStudyRoomReserveTypeResponse> reserveTypeResponses =
                reserveTypeRepository.findAllByStudyRoomId(studyRoom.getId()).stream()
                        .map(GetStudyRoomReserveTypeResponse::from)
                        .toList();;

        List<GetStudyRoomOptionInfoResponse> optionInfoResponses = optionInfoRepository.findAllByStudyRoomId(studyRoom.getId())
                .stream()
                .map(GetStudyRoomOptionInfoResponse::from)
                .toList();

        List<GetStudyRoomTypeInfoResponse> typeInfoResponses = typeReInfoRepository.findAllByStudyRoomId(studyRoom.getId())
                .stream()
                .map(GetStudyRoomTypeInfoResponse::from)
                .toList();

        return GetStudyRoomDetailResponse.of(
                studyRoom,
                likeStatus,
                bookmarkId,
                imageResponses,
                dayOffResponses,
                reserveTypeResponses,
                optionInfoResponses,
                typeInfoResponses
        );
    }

    private Map<Long, LikeStatusAndBookmarkId> getStudyRoomLikeStatusAndBookmarkIdMap(List<StudyRoom> studyRooms, UUID userId) {
        if(userId == null){
            return studyRooms.stream()
                    .collect(Collectors.toMap(StudyRoom::getId, studyRoom -> new LikeStatusAndBookmarkId(false, null)));
        }

        List<Long> studyRoomIds = studyRooms.stream()
                .map(StudyRoom::getId)
                .toList();

        Map<Long, Long> likesMap = likeRepository.findAllByUserIdAndStudyRoomIds(userId, studyRoomIds).stream()
                .collect(Collectors.toMap(StudyRoomLikeInfo::studyRoomId, StudyRoomLikeInfo::id));

        Map<Long, Long> bookmarksMap = bookmarkRepository.findAllByUserIdAndStudyRoomIds(userId, studyRoomIds).stream()
                .collect(Collectors.toMap(StudyRoomBookmarkInfo::studyRoomId, StudyRoomBookmarkInfo::id));

        return studyRoomIds.stream()
                .collect(Collectors.toMap(studyRoomId -> studyRoomId, studyRoomId -> new LikeStatusAndBookmarkId(
                    likesMap.getOrDefault(studyRoomId, null) != null,
                    bookmarksMap.getOrDefault(studyRoomId, null)
                )));
    }

    private LikeStatusAndBookmarkId getLikeStatusAndBookmarkIdByStudyRoomIdAndUserId(Long studyRoomId, UUID userId) {
        if(userId == null)
            return new LikeStatusAndBookmarkId(false, null);

        return new LikeStatusAndBookmarkId(
                likeRepository.existsByStudyRoomIdAndUserId(studyRoomId, userId),
                bookmarkRepository.findIdByStudyRoomIdAndUserId(studyRoomId, userId)
        );
    }

    private record LikeStatusAndBookmarkId(boolean likeStatus, Long bookmarkId) {
    }
}
