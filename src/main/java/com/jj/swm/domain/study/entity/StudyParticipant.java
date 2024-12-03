package com.jj.swm.domain.study.entity;

import com.jj.swm.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Getter
@Entity
@Builder
@Table(name = "study_participant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "study_participant_seq_generator")
    @SequenceGenerator(
            name = "study_participant_seq_generator",
            sequenceName = "study_participant_id_seq",
            allocationSize = 100
    )
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    private StudyParticipantStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_recruitment_position_id", nullable = false)
    private StudyRecruitmentPosition studyRecruitmentPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
