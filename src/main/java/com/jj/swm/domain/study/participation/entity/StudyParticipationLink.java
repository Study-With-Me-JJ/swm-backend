package com.jj.swm.domain.study.participation.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@Table(name = "study_participation_link")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyParticipationLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link", length = 300, nullable = false)
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_participation_id", nullable = false)
    private StudyParticipation participation;
}
