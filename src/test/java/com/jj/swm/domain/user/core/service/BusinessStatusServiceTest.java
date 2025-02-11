package com.jj.swm.domain.user.core.service;

import com.jj.swm.domain.user.core.dto.request.UpgradeRoomAdminRequest;
import com.jj.swm.domain.user.core.service.BusinessStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BusinessStatusServiceTest {

    private final RestClient.Builder restBuilder = RestClient.builder();
    private final MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restBuilder).build();

    private BusinessStatusService businessStatusService;

    @BeforeEach
    void setUp() {
        businessStatusService = new BusinessStatusService(restBuilder.build());

        ReflectionTestUtils.setField(businessStatusService, "businessCheckAPIUrl", "https://api.odcloud.kr/api");
        ReflectionTestUtils.setField(businessStatusService, "businessCheckAPIKey", "test");
        ReflectionTestUtils.setField(businessStatusService, "businessCheckUri", "https://api.odcloud.kr/api?serviceKey=test");
    }

    @Test
    @DisplayName("사업자 정보 조회에 성공하면, valid 01을 반환하고 성공한다.")
    void validateBusinessStatus_whenSuccess_thenReturnValid01_Success() {
        //given
        UpgradeRoomAdminRequest request = UpgradeRoomAdminRequest.builder()
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .build();

        mockRestServiceServer.expect(requestTo("https://api.odcloud.kr/api?serviceKey=test"))
                .andRespond(withSuccess("{\"data\":[{\"valid\":\"01\"}]}", MediaType.APPLICATION_JSON));

        //when
        boolean result = businessStatusService.validateBusinessStatus(request);

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("사업자 정보 조회에 실패하면, valid 02를 반환하고 실패한다.")
    void validateBusinessStatus_whenValidationFail_thenFail() {
        //given
        UpgradeRoomAdminRequest request = UpgradeRoomAdminRequest.builder()
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .build();

        mockRestServiceServer.expect(requestTo("https://api.odcloud.kr/api?serviceKey=test"))
                .andRespond(withSuccess("{\"data\":[{\"valid\":\"02\"}]}", MediaType.APPLICATION_JSON));

        //when
        boolean result = businessStatusService.validateBusinessStatus(request);

        //then
        assertFalse(result);
    }

    @Test
    @DisplayName("사업자 정보 조회 응답값이 없다면 실패한다.")
    void validateBusinessStatus_whenEmptyResponse_thenFail() {
        //given
        UpgradeRoomAdminRequest request = UpgradeRoomAdminRequest.builder()
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .build();

        mockRestServiceServer.expect(requestTo("https://api.odcloud.kr/api?serviceKey=test"))
                .andRespond(withSuccess());

        //when
        boolean result = businessStatusService.validateBusinessStatus(request);

        //then
        assertFalse(result);
    }

    @Test
    @DisplayName("사업자 정보 API 작업 간 오류가 발생하면, 실패한다.")
    void validateBusinessStatus_whenExternalApiError_thenFail() {
        //given
        UpgradeRoomAdminRequest request = UpgradeRoomAdminRequest.builder()
                .businessName("test")
                .businessNumber("0123456789")
                .businessRegistrationDate("20250101")
                .build();

        mockRestServiceServer.expect(requestTo("https://api.odcloud.kr/api?serviceKey=test"))
                .andRespond(withBadRequest());

        //when
        boolean result = businessStatusService.validateBusinessStatus(request);

        //then
        assertFalse(result);
    }

}
