package com.jj.swm.global.common.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    /**
     * 등록일시
     */
    @CreatedDate
    @Column(name = "created_dt", updatable = false, nullable = false)
    protected LocalDateTime createdDt;

    /**
     * 수정일시
     */
    @LastModifiedDate
    @Column(name = "updated_dt", nullable = false)
    protected LocalDateTime updatedDt = LocalDateTime.now();
}
