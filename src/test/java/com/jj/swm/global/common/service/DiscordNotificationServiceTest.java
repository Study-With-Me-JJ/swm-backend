package com.jj.swm.global.common.service;

import com.jj.swm.domain.user.core.dto.event.BusinessVerificationRequestEvent;
import com.jj.swm.domain.user.core.entity.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class DiscordNotificationServiceTest {

    private final RestClient.Builder restBuilder = RestClient.builder();
    private final MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restBuilder).build();

    private DiscordNotificationService discordNotificationService;

    @BeforeEach
    void setUp() {
        discordNotificationService = new DiscordNotificationService(restBuilder.build());
        ReflectionTestUtils.setField(
                discordNotificationService,
                "webhookUrl",
                "https://discord.com/api/webhooks"
        );
    }

    @Test
    @DisplayName("사업자 검수 요청 알림 전송에 성공한다.")
    void sendBusinessVerificationNotification_send_success() {
        //given
        BusinessVerificationRequestEvent event = BusinessVerificationRequestEvent.builder()
                .userId(UUID.randomUUID())
                .userName("test")
                .userRole(RoleType.USER)
                .userNickname("test")
                .businessNumber("0123456789")
                .businessOwnerName("test")
                .businessRegistrationDate("20230101")
                .businessName("test")
                .build();

        mockRestServiceServer.expect(requestTo("https://discord.com/api/webhooks"))
                .andRespond(withNoContent());

        //when
        CompletableFuture<Boolean> response = discordNotificationService.sendBusinessVerificationNotification(event);

        //then
        assertTrue(response.join());
    }

    @Test
    @DisplayName("사업자 검수 요청 알림 전송 상태값이 204가 아니라면 실패한다.")
    void sendBusinessVerificationNotification_whenStatusIsNot204_thenFail() {
        //given
        BusinessVerificationRequestEvent event = BusinessVerificationRequestEvent.builder()
                .userId(UUID.randomUUID())
                .userName("test")
                .userRole(RoleType.USER)
                .userNickname("test")
                .businessNumber("0123456789")
                .businessOwnerName("test")
                .businessRegistrationDate("20230101")
                .businessName("test")
                .build();

        mockRestServiceServer.expect(requestTo("https://discord.com/api/webhooks"))
                .andRespond(withStatus(HttpStatus.OK));

        //when
        CompletableFuture<Boolean> response = discordNotificationService.sendBusinessVerificationNotification(event);

        //then
        assertFalse(response.join());
    }

    @Test
    @DisplayName("사업자 검수 요청 알림 전송에 실패한다.")
    void sendBusinessVerificationNotification_send_Fail() {
        //given
        BusinessVerificationRequestEvent event = BusinessVerificationRequestEvent.builder()
                .userId(UUID.randomUUID())
                .userName("test")
                .userRole(RoleType.USER)
                .userNickname("test")
                .businessNumber("0123456789")
                .businessOwnerName("test")
                .businessRegistrationDate("20230101")
                .businessName("test")
                .build();

        mockRestServiceServer.expect(requestTo("https://discord.com/api/webhooks"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        //when
        CompletableFuture<Boolean> response = discordNotificationService.sendBusinessVerificationNotification(event);

        //then
        assertFalse(response.join());
    }
}
