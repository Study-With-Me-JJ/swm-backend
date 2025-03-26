package com.jj.swm.global.event.handler;

import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationRequestEvent;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationResponseEvent;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.common.enums.RedisPrefix;
import com.jj.swm.global.common.service.KakaoNotificationService;
import com.jj.swm.global.security.jwt.JwtProvider;
import com.jj.swm.global.security.jwt.TokenRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyRoomReservationEventHandler {

    private final KakaoNotificationService kakaoNotificationService;
    private final TokenRedisService tokenRedisService;
    private final JwtProvider jwtProvider;

    @TransactionalEventListener(classes = StudyRoomReservationRequestEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void studyRoomReservationRequestEventAfterCommitHandler(StudyRoomReservationRequestEvent event) {
        String reservationToken = insertReservationToken(event.getStudyRoomReservationInfoId());

        kakaoNotificationService.sendStudyRoomReservationRequestNotification(event, reservationToken).thenAccept(success -> {
            if(!success){
                log.error("카카오 알림 전송 오류, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            } else {
                log.info("카카오 알림 전송 성공, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            }
        });
    }

    @TransactionalEventListener(classes = StudyRoomReservationResponseEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void studyRoomReservationResponseEventAfterCommitHandler(StudyRoomReservationResponseEvent event) {
        tokenRedisService.deleteReservationToken(event.getReservationToken());
        kakaoNotificationService.sendStudyRoomReservationResponseNotification(event).thenAccept(success -> {
            if(!success){
                log.error("카카오 알림 전송 오류, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            } else {
                log.info("카카오 알림 전송 성공, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            }
        });
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
}
