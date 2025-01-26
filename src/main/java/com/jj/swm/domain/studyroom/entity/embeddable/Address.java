package com.jj.swm.domain.studyroom.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Embeddable
public class Address {

    @NotBlank
    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @NotBlank
    @Column(name = "detail_address", nullable = false, length = 100)
    private String detailAddress;

    @NotBlank
    @Column(name = "region", nullable = false, length = 20)
    private String region;

    @NotBlank
    @Column(name = "locality", nullable = false, length = 20)
    private String locality;
}
