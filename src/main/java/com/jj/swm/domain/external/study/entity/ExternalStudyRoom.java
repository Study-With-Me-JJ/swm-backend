package com.jj.swm.domain.external.study.entity;

import com.jj.swm.domain.common.KoreaRegion;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Table(name = "external_study_room")
@Getter
@SQLDelete(sql = "update external_study_room set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExternalStudyRoom extends BaseTimeEntity {
    @Id
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "number", length = 30, nullable = true)
    private String number;

    @Column(name = "url", length = 300, nullable = true)
    private String url;

    @Column(name = "korea_region")
    @Enumerated(EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    private KoreaRegion koreaRegion;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "naver_map_url", length = 300, nullable = false)
    private String naverMapUrl;

    @Column(name = "thumbnail", length = 300, nullable = true)
    private String thumbnail;

    @Column(name = "description", length = 500, nullable = true)
    private String description;
}
