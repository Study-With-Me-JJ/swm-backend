package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.GetStudyRoomCondition;
import com.jj.swm.domain.studyroom.dto.StudyRoomBookmarkInfo;
import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomResponse;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.repository.StudyRoomBookmarkRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomRepository;
import com.jj.swm.global.common.dto.PageResponse;
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
    private final StudyRoomBookmarkRepository studyRoomBookmarkRepository;

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

    private Map<Long, Long> mappingStudyRoomBookmarkAndUser(List<Long> studyRoomIds, UUID userId) {
        return userId != null
                ? studyRoomBookmarkRepository.findAllByUserIdAndStudyRoomIds(userId, studyRoomIds)
                .stream()
                .collect(Collectors.toMap(StudyRoomBookmarkInfo::getId, StudyRoomBookmarkInfo::getStudyRoomId))
                : Collections.emptyMap();
    }
}
