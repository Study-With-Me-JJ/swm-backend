package com.jj.swm.global.common.service;

import com.jj.swm.domain.user.core.dto.event.BusinessVerificationRequestEvent;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationService {

    @Value("${discord.webhook.business-verification-request.url}")
    private String webhookUrl;

    private final RestClient restClient;

    @Async("asyncExecutor")
    public CompletableFuture<Boolean> sendBusinessVerificationNotification(BusinessVerificationRequestEvent event) {
        try{
            ResponseEntity<Void> response = restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildDiscordNotification(event))
                    .retrieve()
                    .toEntity(Void.class);

            if(!response.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT)){
                throw new GlobalException(ErrorCode.EXTERNAL_API_ERROR, "디스코드 알림 전송 오류");
            }

            return CompletableFuture.completedFuture(true);
        } catch (Exception e){
            log.error(e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    public String buildDiscordNotification(BusinessVerificationRequestEvent event) {
        return String.format("""
                {
                  "embeds": [
                    {
                      "title": "사업자 등록 요청",
                      "color": 5814783,
                      "fields": [
                        { "name": "유저 ID", "value": "%s", "inline": true },
                        { "name": "유저 성함", "value": "%s", "inline": true },
                        { "name": "유저 권한", "value": "%s", "inline": true },
                        { "name": "유저 닉네임", "value": "%s", "inline": true },
                        { "name": "유저 이메일", "value": "%s", "inline": true },
                        { "name": "사업자 번호", "value": "%s", "inline": true },
                        { "name": "사업자 소유자", "value": "%s", "inline": true },
                        { "name": "사업자 등록일자", "value": "%s", "inline": true },
                        { "name": "상호명", "value": "%s", "inline": true }
                      ]
                    }
                  ]
                }
                """,
                event.getUserId(),
                event.getUserName(),
                event.getUserRole().name(),
                event.getUserNickname(),
                event.getUserEmail(),
                event.getBusinessNumber(),
                event.getBusinessOwnerName(),
                event.getBusinessRegistrationDate(),
                event.getBusinessName(),
                event.getUserEmail()
        );
    }
}
