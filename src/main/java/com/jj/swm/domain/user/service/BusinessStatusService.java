package com.jj.swm.domain.user.service;

import com.jj.swm.domain.user.dto.request.UpgradeRoomAdminRequest;
import com.jj.swm.domain.user.dto.response.BusinessCheckResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class BusinessStatusService {

    @Value("${business.check.api.url}")
    public String businessCheckAPIUrl;

    @Value("${business.check.api.key}")
    public String businessCheckAPIKey;

    private final RestClient restClient;

    public BusinessStatusService() {
        this.restClient = RestClient.builder().build();
    }

    public boolean validateBusinessStatus(UpgradeRoomAdminRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("b_no", request.getBusinessNumber());
        data.put("p_nm", request.getBusinessName());
        data.put("start_dt", request.getBusinessRegistrationDate());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("businesses", Collections.singletonList(data));

        try {
            BusinessCheckResponse response = restClient.post()
                    .uri(businessCheckAPIUrl + "?serviceKey=" + businessCheckAPIKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (clientRequest, clientResponse) -> {
                        log.error("External API Error, status code: {}", clientResponse.getStatusCode());
                        throw new GlobalException(ErrorCode.EXTERNAL_API_ERROR, "External API Error");
                    })
                    .body(BusinessCheckResponse.class);

            if(response != null && response.getData() != null && !response.getData().isEmpty()) {
                String valid = response.getData().get(0).getValid();
                return "01".equals(valid);
            }
        } catch (Exception e) {
            log.error("Failed to validate business status: {}", e.getMessage(), e);
        }

        return false;
    }
}