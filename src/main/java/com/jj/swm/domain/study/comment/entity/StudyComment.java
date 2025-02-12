package com.jj.swm.domain.study.comment.entity;

import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.user.entity.User;
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
            User user,
            Study study,
            UpsertCommentRequest createRequest
    ) {
        return StudyComment.builder()
                .user(user)
                .study(study)
                .content(createRequest.getContent())
                .build();
    }

    public void addParent(StudyComment parent) {
        parent.getChildren().add(this);
        this.parent = parent;
    }

    public void modify(UpsertCommentRequest updateRequest) {
        this.content = updateRequest.getContent();
    }
}
