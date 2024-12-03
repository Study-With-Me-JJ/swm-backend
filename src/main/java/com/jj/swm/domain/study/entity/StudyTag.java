package com.jj.swm.domain.study.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "study_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update study_tag set deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
public class StudyTag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "study_tag_seq_generator")
    @SequenceGenerator(
            name = "study_tag_seq_generator",
            sequenceName = "study_tag_id_seq",
            allocationSize = 100
    )
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;
}
