package com.jj.swm.domain.study.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@Table(name = "study_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "study_image_seq_generator")
    @SequenceGenerator(
            name = "study_image_seq_generator",
            sequenceName = "study_image_id_seq",
            allocationSize = 100
    )
    private Long id;

    @Column(name = "image_url", length = 300, nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;
}
