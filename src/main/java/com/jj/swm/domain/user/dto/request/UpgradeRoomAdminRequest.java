package com.jj.swm.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpgradeRoomAdminRequest {

    @NotBlank
    @Pattern(
            regexp = "\\d{10}$",
            message = "사업자 등록 번호는 10자리 숫자로만 입력해야 함."
    )
    private String businessNumber;

    @NotBlank
    private String businessName;

    @NotBlank
    private String businessRegistrationDate;
}