package com.jj.swm.global.event.handler;

import com.jj.swm.domain.user.dto.event.BusinessVerificationRequestEvent;
import com.jj.swm.global.common.service.DiscordNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessVerificationEventHandler {

    private final DiscordNotificationService discordNotificationService;

    @TransactionalEventListener(classes = BusinessVerificationRequestEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void businessVerificationRequestEventAfterCommitHandler(BusinessVerificationRequestEvent event) {
        discordNotificationService.sendBusinessVerificationNotification(event).thenAccept(success -> {
            if(!success){
                log.error("디스코드 알림 전송 오류, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            } else {
                log.info("디스코드 알림 전송 성공, time: {}", LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            }
        });
    }
}
