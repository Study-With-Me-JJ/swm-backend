package com.jj.swm.domain.study.entity;

import com.jj.swm.domain.study.dto.request.CommentUpsertRequest;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@Table(name = "study_comment")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update study_comment set deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
public class StudyComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private StudyComment parent;

    @OneToMany(mappedBy = "parent")
    private List<StudyComment> children = new ArrayList<>();

    public static StudyComment of(
            Study study,
            User user,
            CommentUpsertRequest commentUpsertRequest
    ) {
        return StudyComment.builder()
                .content(commentUpsertRequest.getContent())
                .study(study)
                .user(user)
                .build();
    }

    public void addParent(StudyComment parent) {
        parent.getChildren().add(this);
        this.parent = parent;
    }

    public void modify(CommentUpsertRequest commentUpdateRequest) {
        this.content = commentUpdateRequest.getContent();
    }
}
