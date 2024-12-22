package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.dto.StudyRoomBookmarkInfo;
import com.jj.swm.domain.studyroom.dto.response.*;
import com.jj.swm.domain.studyroom.entity.*;
import com.jj.swm.domain.studyroom.repository.*;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${studyroom.page.size}")
    private int studyRoomPageSize;

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomResponse> getStudyRooms(
            GetStudyRoomCondition condition,
            UUID userId
    ) {
        List<StudyRoom> studyRooms
                = studyRoomRepository.findAllWithPaginationAndCondition(studyRoomPageSize + 1, condition);

        if(studyRooms.isEmpty()) {
            return PageResponse.of(List.of(), false);
        }

        boolean hasNext = studyRooms.size() > studyRoomPageSize;

        List<StudyRoom> pagedStudyRooms = hasNext ? studyRooms.subList(0, studyRoomPageSize) : studyRooms;

        List<Long> studyRoomIds = pagedStudyRooms.stream()
                .map(StudyRoom::getId)
                .toList();

        Map<Long, Long> bookmarkIdByStudyRoomId
                = mappingStudyRoomBookmarkAndUser(studyRoomIds, userId);

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

    private Map<Long, Long> mappingStudyRoomBookmarkAndUser(List<Long> studyRoomIds, UUID userId) {
        return userId != null
                ? bookmarkRepository.findAllByUserIdAndStudyRoomIds(userId, studyRoomIds)
                .stream()
                .collect(Collectors.toMap(StudyRoomBookmarkInfo::getId, StudyRoomBookmarkInfo::getStudyRoomId))
                : Collections.emptyMap();
    }
}
