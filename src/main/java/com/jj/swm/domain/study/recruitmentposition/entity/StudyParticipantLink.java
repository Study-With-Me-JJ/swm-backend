package com.jj.swm.domain.study.recruitmentposition.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@Table(name = "study_participant_link")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyParticipantLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link", length = 300, nullable = false)
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_participant_id", nullable = false)
    private StudyParticipant participant;
}
