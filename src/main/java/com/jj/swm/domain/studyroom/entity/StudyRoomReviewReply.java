package com.jj.swm.domain.studyroom.entity;

import com.jj.swm.domain.user.entity.User;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@SQLDelete(sql = "UPDATE study_room_review_reply SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "study_room_review_reply")
public class StudyRoomReviewReply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reply", nullable = false)
    private String reply;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_review_id", nullable = false)
    private StudyRoomReview studyRoomReview;

    public static StudyRoomReviewReply of(String reply, StudyRoomReview studyRoomReview, User user) {
        return StudyRoomReviewReply.builder()
                .reply(reply)
                .studyRoomReview(studyRoomReview)
                .user(user)
                .build();
    }

    public void modifyReply(String reply) {
        this.reply = reply;
    }
}
