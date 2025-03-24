package com.jj.swm.global.event.handler;

import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationRequestEvent;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationResponseEvent;
import com.jj.swm.global.common.service.KakaoNotificationService;
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

    @TransactionalEventListener(classes = StudyRoomReservationRequestEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void studyRoomReservationRequestEventAfterCommitHandler(StudyRoomReservationRequestEvent event) {
        kakaoNotificationService.sendStudyRoomReservationRequestNotification(event).thenAccept(success -> {
            if(!success){
                log.error("카카오 알림 전송 오류, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            } else {
                log.info("카카오 알림 전송 성공, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            }
        });
    }

    @TransactionalEventListener(classes = StudyRoomReservationResponseEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void studyRoomReservationResponseEventAfterCommitHandler(StudyRoomReservationResponseEvent event) {
        kakaoNotificationService.sendStudyRoomReservationResponseNotification(event).thenAccept(success -> {
            if(!success){
                log.error("카카오 알림 전송 오류, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            } else {
                log.info("카카오 알림 전송 성공, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            }
        });
    }
}
