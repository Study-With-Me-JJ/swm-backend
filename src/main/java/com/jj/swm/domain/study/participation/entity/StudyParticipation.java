package com.jj.swm.domain.study.participation.entity;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.participation.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.entity.embeddable.FileInfo;
import com.jj.swm.domain.user.core.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "study_participation")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update study_participation set deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
public class StudyParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    private StudyParticipationStatus status;

    @Column(name = "kakao_id", length = 50, nullable = false)
    private String kakaoId;

    @Column(name = "cover_letter", nullable = false)
    private String coverLetter;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Embedded
    private FileInfo fileInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_recruitment_position_id", nullable = false)
    private StudyRecruitmentPosition recruitmentPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static StudyParticipation of(
            CreateStudyParticipationRequest request,
            Study study,
            StudyRecruitmentPosition recruitmentPosition,

            User user
    ) {
        return StudyParticipation.builder()
                .status(StudyParticipationStatus.PENDING)
                .kakaoId(request.getKakaoId())
                .coverLetter(request.getCoverLetter())
                .fileInfo(request.getFileInfo())
                .study(study)
                .recruitmentPosition(recruitmentPosition)
                .user(user)
                .build();
    }

    public void modifyStatus(StudyParticipationStatus status) {
        this.status = status;
    }

    public void modifyPosition(StudyRecruitmentPosition recruitmentPosition) {
        this.recruitmentPosition = recruitmentPosition;
    }

    public void modify(UpdateStudyParticipationRequest request) {
        this.kakaoId = request.getKakaoId();
        this.coverLetter = request.getCoverLetter();
        this.fileInfo = request.getFileInfo();
    }
}
