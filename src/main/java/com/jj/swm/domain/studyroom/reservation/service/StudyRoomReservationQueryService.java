package com.jj.swm.domain.studyroom.reservation.service;

import com.jj.swm.domain.studyroom.reservation.dto.response.GetReservationInfoDetailsResponse;
import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import com.jj.swm.domain.studyroom.reservation.repository.StudyRoomReservationInfoRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import com.jj.swm.global.security.jwt.TokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyRoomReservationQueryService {

    private final TokenRedisService tokenRedisService;

    private final StudyRoomReservationInfoRepository reservationInfoRepository;

    @Transactional(readOnly = true)
    public GetReservationInfoDetailsResponse getReservationInfoDetails(String reservationToken) {
        Long studyRoomReservationInfoId
                = tokenRedisService.findReservationIdByReservationTokenOrThrow(reservationToken);

        StudyRoomReservationInfo studyRoomReservationInfo = reservationInfoRepository.findByIdWithReserveType(studyRoomReservationInfoId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReservationInfo Not Found"));

        return GetReservationInfoDetailsResponse.from(studyRoomReservationInfo);
    }

    @Transactional(readOnly = true)
    public GetReservationInfoDetailsResponse getReservationInfoDetails(Long studyRoomReservationInfoId) {
        StudyRoomReservationInfo studyRoomReservationInfo = reservationInfoRepository.findByIdWithReserveType(studyRoomReservationInfoId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReservationInfo Not Found"));

        return GetReservationInfoDetailsResponse.from(studyRoomReservationInfo);
    }
}
