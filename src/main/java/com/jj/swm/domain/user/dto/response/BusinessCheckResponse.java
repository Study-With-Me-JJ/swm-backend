package com.jj.swm.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessCheckResponse {

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("request_cnt")
    private int requestCount;

    @JsonProperty("valid_cnt")
    private int validCount;

    @JsonProperty("data")
    private List<BusinessCheckData> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusinessCheckData {

        @JsonProperty("b_no")
        private String businessNumber;

        @JsonProperty("valid")
        private String valid;

        @JsonProperty("valid_msg")
        private String validMessage;

        // 기타 필요한 필드 추가 가능
    }
}
