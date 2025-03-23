package com.jj.swm.domain.studyroom.reservation.entity;

import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.reservation.dto.request.CreateStudyRoomReservationRequest;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Getter
@SQLDelete(sql = "UPDATE study_room_reservation_info SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "study_room_reservation_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StudyRoomReservationInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reserver_name", nullable = false, length = 20)
    private String reserverName;

    @Column(name = "reserver_phone_number", nullable = false, length = 20)
    private String reserverPhoneNumber;

    @Column(name = "headcount", nullable = false)
    private Integer headcount;

    @Column(name = "memo", length = 300)
    private String memo;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time", nullable = false)
    private LocalDateTime checkOutTime;

    @Column(name = "usage_time", nullable = false)
    private Integer usageTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    private ApprovalStatus approvalStatus;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_reserve_type_id", nullable = false)
    private StudyRoomReserveType studyRoomReserveType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    public static StudyRoomReservationInfo of(
            CreateStudyRoomReservationRequest request,
            StudyRoom studyRoom,
            StudyRoomReserveType studyRoomReserveType,
            User user
    ) {
        return StudyRoomReservationInfo.builder()
                .reserverName(request.getReserverName())
                .reserverPhoneNumber(request.getReserverPhoneNumber())
                .headcount(request.getHeadcount())
                .memo(request.getMemo())
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckInTime().plusHours(request.getUsageTime()))
                .usageTime(request.getUsageTime())
                .approvalStatus(ApprovalStatus.WAITING)
                .user(user)
                .studyRoomReserveType(studyRoomReserveType)
                .studyRoom(studyRoom)
                .build();
    }
}
