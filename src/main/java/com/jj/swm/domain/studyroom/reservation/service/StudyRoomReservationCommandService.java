package com.jj.swm.domain.studyroom.reservation.service;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomReserveTypeRepository;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationRequestEvent;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationResponseEvent;
import com.jj.swm.domain.studyroom.reservation.dto.request.CreateStudyRoomReservationRequest;
import com.jj.swm.domain.studyroom.reservation.dto.request.UpdateStudyRoomReservationApprovalStatusRequest;
import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import com.jj.swm.domain.studyroom.reservation.repository.StudyRoomReservationInfoRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.event.Events;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyRoomReservationCommandService {

    private final StudyRoomReservationInfoRepository reservationInfoRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomReserveTypeRepository reserveTypeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStudyRoomReservationAndSendSms(CreateStudyRoomReservationRequest request, UUID userId){
        StudyRoomReserveType studyRoomReserveType = reserveTypeRepository.findById(request.getStudyRoomReserveTypeId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReserveType Not Found"));

        StudyRoom studyRoom = studyRoomRepository.getReferenceById(
                studyRoomReserveType.getStudyRoom().getId()
        );

        User user = userRepository.getReferenceById(userId);

        StudyRoomReservationInfo studyRoomReservationInfo = StudyRoomReservationInfo.of(
                request,
                studyRoom,
                studyRoomReserveType,
                user
        );

        studyRoomReservationInfo = reservationInfoRepository.save(studyRoomReservationInfo);

        Events.send(StudyRoomReservationRequestEvent.from(studyRoomReservationInfo));
        // TODO: 알림톡 전송 작업
    }

    @Transactional
    public void updateStudyRoomReservationApprovalStatusAndSendSms(
            UpdateStudyRoomReservationApprovalStatusRequest request, Long studyRoomReservationInfoId
    ){
        StudyRoomReservationInfo studyRoomReservationInfo = reservationInfoRepository.findByIdWithStudyRoom(studyRoomReservationInfoId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReservationInfo Not Found"));

        studyRoomReservationInfo.modifyApprovalStatus(request.getApprovalStatus());

        Events.send(StudyRoomReservationResponseEvent.from(studyRoomReservationInfo));
        // TODO: 알림톡 전송 작업
    }
}
