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
@SQLDelete(sql = "UPDATE study_room_reserve_type SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "study_room_reserve_type")
public class StudyRoomReserveType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "max_headcount", nullable = false)
    private int maxHeadcount;

    @Column(name = "reservation_option", nullable = false)
    private String reservationOption;

    @Column(name = "price_per_hour", nullable = false)
    private int pricePerHour;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;
}
