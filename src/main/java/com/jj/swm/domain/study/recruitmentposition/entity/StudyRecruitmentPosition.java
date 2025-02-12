package com.jj.swm.domain.study.recruitmentposition.entity;

import com.jj.swm.domain.study.recruitmentposition.dto.request.AddRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.ModifyRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.entity.Study;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "study_recruitment_position")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public static StudyRecruitmentPosition of(Study study, AddRecruitmentPositionRequest request) {
        return StudyRecruitmentPosition.builder()
                .title(request.getTitle())
                .headcount(request.getHeadcount())
                .acceptedCount(0)
                .study(study)
                .build();
    }

    public void modify(ModifyRecruitmentPositionRequest request) {
        this.title = request.getTitle();
        this.headcount = request.getHeadcount();
        this.acceptedCount = request.getAcceptedCount();
    }
}
