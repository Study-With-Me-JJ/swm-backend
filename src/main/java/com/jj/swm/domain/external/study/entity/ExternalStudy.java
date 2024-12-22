package com.jj.swm.domain.external.study.entity;

import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@Table(name = "external_study")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ExternalStudy extends BaseTimeEntity {
    @Id
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "technologies", nullable = true)
    private String technologies;

    @Column(name = "roles", nullable = true)
    private String roles;

    @Column(name = "deadline_date", nullable = true)
    private LocalDate deadlineDate;
}
