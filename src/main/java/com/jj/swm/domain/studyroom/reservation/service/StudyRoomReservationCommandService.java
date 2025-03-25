package com.jj.swm.domain.studyroom.reservation.service;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomReserveTypeRepository;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationRequestEvent;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationResponseEvent;
import com.jj.swm.domain.studyroom.reservation.dto.request.CreateStudyRoomReservationRequest;
import com.jj.swm.domain.studyroom.reservation.dto.request.UpdateStudyRoomReservationApprovalStatusRequest;
import com.jj.swm.domain.studyroom.reservation.dto.request.UpdateStudyRoomReservationRequest;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import com.jj.swm.domain.studyroom.reservation.repository.StudyRoomReservationInfoRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.common.enums.RedisPrefix;
import com.jj.swm.global.event.Events;
import com.jj.swm.global.exception.GlobalException;
import com.jj.swm.global.security.jwt.JwtProvider;
import com.jj.swm.global.security.jwt.TokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyRoomReservationCommandService {

    private final TokenRedisService tokenRedisService;
    private final JwtProvider jwtProvider;

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

        String reservationToken = insertReservationToken(studyRoomReservationInfo.getId());

        Events.send(StudyRoomReservationRequestEvent.of(studyRoomReservationInfo, reservationToken));
        // TODO: 알림톡 전송 작업
    }

    @Transactional
    public void updateStudyRoomReservationApprovalStatusAndSendSms(
            UpdateStudyRoomReservationApprovalStatusRequest request, Long studyRoomReservationInfoId
    ){
        StudyRoomReservationInfo reservationInfo = reservationInfoRepository.findByIdWithStudyRoom(studyRoomReservationInfoId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReservationInfo Not Found"));

        if(reservationInfo.getApprovalStatus().equals(ApprovalStatus.CANCELED))
            throw new GlobalException(ErrorCode.NOT_VALID, "StudyRoomReservationInfo Already Canceled");

        reservationInfo.modifyApprovalStatus(request.getApprovalStatus());

        Events.send(StudyRoomReservationResponseEvent.from(reservationInfo));
        // TODO: 알림톡 전송 작업
    }

    @Transactional
    public void updateStudyRoomReservation(
            UpdateStudyRoomReservationRequest request,
            Long studyRoomReservationInfoId,
            UUID userId
    ) {
        StudyRoomReservationInfo reservationInfo = findByIdAndUserIdOrThrow(studyRoomReservationInfoId, userId);

        reservationInfo.modifyStudyRoomReservationInfo(request);
    }

    @Transactional
    public void cancelStudyRoomReservation(Long studyRoomReservationInfoId, UUID userId) {
        StudyRoomReservationInfo reservationInfo = findByIdAndUserIdOrThrow(studyRoomReservationInfoId, userId);

        reservationInfo.modifyApprovalStatus(ApprovalStatus.CANCELED);
    }

    private String insertReservationToken(Long studyRoomReservationInfoId) {
        String reservationToken = jwtProvider.generateTokenForReservation(
                studyRoomReservationInfoId, ExpirationTime.STUDYROOM_RESERVATION_TOKEN.getValue()
        );

        tokenRedisService.saveReservationToken(
                RedisPrefix.STUDYROOM_RESERVATION_TOKEN.getValue() + reservationToken,
                studyRoomReservationInfoId.toString()
        );

        return reservationToken;
    }

    private StudyRoomReservationInfo findByIdAndUserIdOrThrow(Long studyRoomReservationInfoId, UUID userId) {
        StudyRoomReservationInfo reservationInfo = reservationInfoRepository.findByIdAndUserId(studyRoomReservationInfoId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReservationInfo Not Found"));

        return reservationInfo;
    }
}
