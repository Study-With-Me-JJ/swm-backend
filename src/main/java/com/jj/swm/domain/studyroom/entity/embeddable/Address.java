package com.jj.swm.domain.studyroom.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Address {

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "detail_address", nullable = false, length = 100)
    private String detailAddress;

    @Column(name = "region", nullable = false, length = 20)
    private String region;

    @Column(name = "locality", nullable = false, length = 20)
    private String locality;
}
