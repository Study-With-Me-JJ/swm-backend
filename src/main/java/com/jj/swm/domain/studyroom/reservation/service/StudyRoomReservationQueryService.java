package com.jj.swm.domain.studyroom.reservation.service;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.reservation.dto.response.GetReservationInfoDetailsResponse;
import com.jj.swm.domain.studyroom.reservation.dto.response.GetReservationInfoResponse;
import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import com.jj.swm.domain.studyroom.reservation.repository.StudyRoomReservationInfoRepository;
import com.jj.swm.domain.studyroom.reservation.repository.custom.ReservationInfoResponse;
import com.jj.swm.global.common.constants.PageSize;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import com.jj.swm.global.security.jwt.TokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyRoomReservationQueryService {

    private final TokenRedisService tokenRedisService;

    private final StudyRoomReservationInfoRepository reservationInfoRepository;
    private final StudyRoomRepository studyRoomRepository;

    @Transactional(readOnly = true)
    public GetReservationInfoDetailsResponse getReservationInfoDetails(String reservationToken) {
        Long studyRoomReservationInfoId
                = tokenRedisService.findReservationIdByReservationTokenOrThrow(reservationToken);

        StudyRoomReservationInfo studyRoomReservationInfo = reservationInfoRepository.findByIdWithReserveType(studyRoomReservationInfoId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReservationInfo Not Found"));

        return GetReservationInfoDetailsResponse.from(studyRoomReservationInfo);
    }

    @Transactional(readOnly = true)
    public GetReservationInfoDetailsResponse getReservationInfoDetails(Long studyRoomReservationInfoId, UUID userId) {
        StudyRoomReservationInfo studyRoomReservationInfo = reservationInfoRepository.findByIdAndUserIdWithReserveType(studyRoomReservationInfoId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReservationInfo Not Found"));

        return GetReservationInfoDetailsResponse.from(studyRoomReservationInfo);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetReservationInfoResponse> getReservationInfos(UUID userId, int pageNo) {
        List<Long> studyRoomIds = studyRoomRepository.findStudyRoomIdsByUserId(userId);

        Pageable pageable = PageRequest.of(pageNo, PageSize.StudyRoomReservationInfo, Sort.by("id").descending());

        Page<ReservationInfoResponse> pagedReservationInfos
                = reservationInfoRepository.findPagedReservationInfoByStudyRoomIds(studyRoomIds, pageable);

        return PageResponse.of(pagedReservationInfos, GetReservationInfoResponse::from);
    }
}
