package com.jj.swm.domain.study.entity;

import com.jj.swm.domain.study.dto.request.RecruitPositionUpsertRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "study_recruitment_position")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update study_recruitment_position set deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
public class StudyRecruitmentPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "headcount", nullable = false)
    private Integer headcount;

    @Column(name = "accepted_count", nullable = false)
    private int acceptedCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    public static StudyRecruitmentPosition of(Study study, RecruitPositionUpsertRequest createRequest) {
        return StudyRecruitmentPosition.builder()
                .title(createRequest.getTitle())
                .headcount(createRequest.getHeadcount())
                .acceptedCount(0)
                .study(study)
                .build();
    }

    public void modify(RecruitPositionUpsertRequest updateRequest) {
        this.title = updateRequest.getTitle();
        this.headcount = updateRequest.getHeadcount();
    }
}
