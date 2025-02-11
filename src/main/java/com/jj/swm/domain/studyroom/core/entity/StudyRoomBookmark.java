package com.jj.swm.domain.studyroom.core.entity;

import com.jj.swm.domain.user.core.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "study_room_bookmark")
public class StudyRoomBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    public static StudyRoomBookmark of(StudyRoom studyRoom, User user) {
        return StudyRoomBookmark.builder()
                .studyRoom(studyRoom)
                .user(user)
                .build();
    }
}
