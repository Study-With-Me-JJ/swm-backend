package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomCreateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomReservationTypeCreateRequest;
import com.jj.swm.domain.studyroom.entity.*;
import com.jj.swm.domain.studyroom.repository.*;
import com.jj.swm.domain.user.entity.RoleType;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyRoomCommandService {

    private final StudyRoomRepository studyRoomRepository;
    private final UserRepository userRepository;
    private final StudyRoomDayOffRepository dayOffRepository;
    private final StudyRoomImageRepository imageRepository;
    private final StudyRoomOptionInfoRepository optionInfoRepository;
    private final StudyRoomTypeInfoRepository typeInfoRepository;
    private final StudyRoomReserveTypeRepository reserveTypeRepository;
    private final StudyRoomTagRepository tagRepository;

    @Transactional
    public void create(StudyRoomCreateRequest request, UUID userId){
        User user = validateUser(userId);

        StudyRoom studyRoom = StudyRoom.of(request);
        studyRoom.modifyRoomAdmin(user);

        studyRoom = studyRoomRepository.save(studyRoom);

        createAllOfStudyRoomRelatedInfo(studyRoom, request);
    }

    public void createAllOfStudyRoomRelatedInfo(StudyRoom studyRoom, StudyRoomCreateRequest request) {
        dayOffRepository.batchInsert(request.getDayOffs(), studyRoom);
        tagRepository.batchInsert(request.getTags(), studyRoom);
        validateImages(request.getImageUrls(), studyRoom);
        validateOptions(request.getOptions(), studyRoom);
        validateTypes(request.getTypes(), studyRoom);
        validateReservationTypes(request.getReservationTypes(), studyRoom);
    }

    private void validateImages(List<String> imageUrls, StudyRoom studyRoom) {
        // 이미지 URL을 StudyRoom과 연결하여 저장
        if (imageUrls != null) {
            imageRepository.batchInsert(imageUrls, studyRoom);
        }
    }

    private void validateOptions(List<StudyRoomOption> options, StudyRoom studyRoom) {
        // 옵션 생성 및 저장
        if (options != null) {
            optionInfoRepository.batchInsert(options, studyRoom);
        }
    }

    private void validateTypes(List<StudyRoomType> types, StudyRoom studyRoom) {
        // StudyRoomType 엔티티 생성 및 저장
        if (types != null) {
            typeInfoRepository.batchInsert(types, studyRoom);
        }
    }

    private void validateReservationTypes(
            List<StudyRoomReservationTypeCreateRequest> reservationTypes, StudyRoom studyRoom
    ) {
        // 예약 유형 생성 및 저장
        if (reservationTypes != null) {
            reserveTypeRepository.batchInsert(reservationTypes, studyRoom);
        }
    }

    private User validateUser(UUID userId) {
        return userRepository.findByIdAndUserRole(userId, RoleType.ROOM_ADMIN)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "User Not Found"));
    }
}
