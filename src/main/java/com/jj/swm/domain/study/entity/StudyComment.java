package com.jj.swm.domain.study.entity;

import com.jj.swm.domain.user.entity.User;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "study_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update study_comment set deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
public class StudyComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "study_comment_seq_generator")
    @SequenceGenerator(
            name = "study_comment_seq_generator",
            sequenceName = "study_comment_id_seq",
            allocationSize = 100
    )
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
