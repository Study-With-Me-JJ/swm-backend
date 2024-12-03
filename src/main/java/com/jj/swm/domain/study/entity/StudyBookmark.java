package com.jj.swm.domain.study.entity;

import com.jj.swm.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@Table(name = "study_bookmark")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "study_bookmark_seq_generator")
    @SequenceGenerator(
            name = "study_bookmark_seq_generator",
            sequenceName = "study_bookmark_id_seq",
            allocationSize = 100
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
