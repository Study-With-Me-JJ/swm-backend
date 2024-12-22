package com.jj.swm.domain.user.entity;

import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User extends BaseTimeEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "nickname", nullable = false, length = 50, unique = true)
    private String nickname;

    @Column(name = "profile_image_url", length = 300)
    private String profileImageUrl;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    private RoleType userRole;

    @OneToMany(mappedBy = "user")
    private List<StudyRoom> studyRooms = new ArrayList<>();

    public void addStudyRoom(StudyRoom studyRoom) {
        studyRooms.add(studyRoom);
    }
}
