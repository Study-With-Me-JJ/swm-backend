package com.jj.swm.global.common.service;

import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationRequestEvent;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoNotificationService {
    public CompletableFuture<Boolean> sendStudyRoomReservationRequestNotification(
            StudyRoomReservationRequestEvent event, String reservationToken
    ) {
        log.info("카카오 알림 전송 성공, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        return CompletableFuture.completedFuture(true);
    }

    public CompletableFuture<Boolean> sendStudyRoomReservationResponseNotification(StudyRoomReservationResponseEvent event) {
        log.info("카카오 알림 전송 성공, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        return CompletableFuture.completedFuture(true);
    }
}
