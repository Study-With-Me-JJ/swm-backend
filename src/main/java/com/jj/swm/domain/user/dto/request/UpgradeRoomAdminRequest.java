package com.jj.swm.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    @Schema(description = "사업자 번호", example = "0123456789")
    private String businessNumber;

    @NotBlank
    @Schema(description = "사업자 대표명", example = "사업자 대표 명")
    private String businessOwnerName;

    @NotBlank
    @Pattern(
            regexp = "\\d{8}$",
            message = "사업자 등록 날짜는 8자리 숫자로만 입력해야 함."
    )
    @Schema(description = "사업자 등록 일자", example = "20241231")
    private String businessRegistrationDate;

    @NotBlank
    @Schema(description = "상호명", example = "상호명")
    private String businessName;
}
