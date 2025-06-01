package com.jj.swm.global.common.dto.component;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BaseTimeInfo {

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime updatedAt;

    public static BaseTimeInfo of(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return BaseTimeInfo.builder()
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
