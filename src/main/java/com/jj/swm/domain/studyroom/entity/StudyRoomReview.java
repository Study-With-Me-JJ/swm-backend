package com.jj.swm.domain.studyroom.entity;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomReviewUpdateRequest;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@SQLDelete(sql = "UPDATE study_room_review SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "study_room_review")
public class StudyRoomReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    public static StudyRoomReview of(String comment, int rating, StudyRoom studyRoom, User user) {
        return StudyRoomReview.builder()
                .comment(comment)
                .rating(rating)
                .studyRoom(studyRoom)
                .user(user)
                .build();
    }

    public void modifyReview(StudyRoomReviewUpdateRequest request) {
        this.comment = request.getComment();
        this.rating = request.getRating();
    }
}
