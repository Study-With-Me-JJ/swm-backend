package com.jj.swm.domain.studyroom.core.service;

import com.jj.swm.domain.studyroom.core.dto.response.*;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomBookmark;
import com.jj.swm.domain.studyroom.core.repository.*;
import com.jj.swm.domain.studyroom.core.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.core.dto.StudyRoomBookmarkInfo;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.PageSize;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
                = studyRoomRepository.findAllWithPaginationAndCondition(PageSize.StudyRoom + 1, condition);

        if(studyRooms.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = studyRooms.size() > PageSize.StudyRoom;

        List<StudyRoom> pagedStudyRooms = hasNext ? studyRooms.subList(0, PageSize.StudyRoom) : studyRooms;

        Map<Long, Long> bookmarkIdByStudyRoomId = getStudyRoomBookmarkMapping(pagedStudyRooms, userId);

        List<GetStudyRoomResponse> responses = pagedStudyRooms.stream()
                .map(studyRoom -> GetStudyRoomResponse.of(
                                studyRoom,
                        bookmarkIdByStudyRoomId.getOrDefault(studyRoom.getId(), null)))
                .toList();

        return PageResponse.of(responses, hasNext);
    }

    @Transactional(readOnly = true)
    public GetStudyRoomDetailResponse getStudyRoomDetail(Long studyRoomId, UUID userId) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithTags(studyRoomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoom Not Found"));

        boolean likeStatus = userId != null && likeRepository.existsByStudyRoomIdAndUserId(studyRoomId, userId);

        return createAllOfStudyRoomRelatedResponse(studyRoomId, likeStatus, studyRoom);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomResponse> getLikedStudyRooms(int pageNo, UUID userId) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyRoom,
                Sort.by("id").descending()
        );

        Page<StudyRoom> pagedStudyRoomLike
                = likeRepository.findPagedStudyRoomByUserId(userId, pageable);

        Map<Long, Long> bookmarkIdByStudyRoomId
                = getStudyRoomBookmarkMapping(pagedStudyRoomLike.getContent(), userId);

        return PageResponse.of(
                pagedStudyRoomLike,
                (studyRoom) ->
                        GetStudyRoomResponse.of(studyRoom, bookmarkIdByStudyRoomId.getOrDefault(studyRoom.getId(), null))
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomResponse> getBookmarkedStudyRooms(int pageNo, UUID userId) {
        Pageable pageable = PageRequest.of(
                pageNo,
                PageSize.StudyRoom,
                Sort.by("id").descending()
        );

        Page<StudyRoomBookmark> pagedStudyRoomBookmark = bookmarkRepository.findPagedBookmarkByUserIdWithStudyRoom(
                userId, pageable
        );

        return PageResponse.of(pagedStudyRoomBookmark, GetStudyRoomResponse::of);
    }

    private GetStudyRoomDetailResponse createAllOfStudyRoomRelatedResponse(
            Long studyRoomId,
            boolean likeStatus,
            StudyRoom studyRoom
    ) {
        List<GetStudyRoomImageResponse> imageResponses = imageRepository.findAllByStudyRoomId(studyRoomId).stream()
                .map(GetStudyRoomImageResponse::from)
                .toList();;

        List<GetStudyRoomDayOffResponse> dayOffResponses = dayOffRepository.findAllByStudyRoomId(studyRoomId).stream()
                .map(GetStudyRoomDayOffResponse::from)
                .toList();;

        List<GetStudyRoomReserveTypeResponse> reserveTypeResponses =
                reserveTypeRepository.findAllByStudyRoomId(studyRoomId).stream()
                        .map(GetStudyRoomReserveTypeResponse::from)
                        .toList();;

        List<GetStudyRoomOptionInfoResponse> optionInfoResponses = optionInfoRepository.findAllByStudyRoomId(studyRoomId)
                .stream()
                .map(GetStudyRoomOptionInfoResponse::from)
                .toList();

        List<GetStudyRoomTypeInfoResponse> typeInfoResponses = typeReInfoRepository.findAllByStudyRoomId(studyRoomId)
                .stream()
                .map(GetStudyRoomTypeInfoResponse::from)
                .toList();

        return GetStudyRoomDetailResponse.of(
                studyRoom,
                likeStatus,
                imageResponses,
                dayOffResponses,
                reserveTypeResponses,
                optionInfoResponses,
                typeInfoResponses
        );
    }

    private Map<Long, Long> getStudyRoomBookmarkMapping(List<StudyRoom> studyRooms, UUID userId) {
        List<Long> studyRoomIds = studyRooms.stream()
                .map(StudyRoom::getId)
                .toList();

        return userId != null
                ? bookmarkRepository.findAllByUserIdAndStudyRoomIds(userId, studyRoomIds)
                .stream()
                .collect(Collectors.toMap(StudyRoomBookmarkInfo::getStudyRoomId, StudyRoomBookmarkInfo::getId))
                : Collections.emptyMap();
    }
}
