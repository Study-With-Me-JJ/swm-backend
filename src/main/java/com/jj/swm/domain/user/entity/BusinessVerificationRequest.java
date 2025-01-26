package com.jj.swm.domain.user.entity;

import com.jj.swm.domain.user.dto.request.UpgradeRoomAdminRequest;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Getter
@SQLDelete(sql = "UPDATE business_verification_request SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "business_verification_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BusinessVerificationRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_name", nullable = false, length = 30)
    private String userName;

    @Column(name = "user_role", nullable = false)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING)
    private RoleType userRole;

    @Column(name = "user_nickname", nullable = false, length = 50)
    private String userNickname;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "business_number", unique = true, nullable = false, length = 10)
    private String businessNumber;

    @Column(name = "business_owner_name", nullable = false, length = 30)
    private String businessOwnerName;

    @Column(name = "business_registration_date", nullable = false, length = 10)
    private String businessRegistrationDate;

    @Column(name = "business_name", nullable = false, length = 50)
    private String businessName;

    @Column(name = "inspection_status", nullable = false)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING)
    private InspectionStatus inspectionStatus;

    public static BusinessVerificationRequest of(User user, String userEmail, UpgradeRoomAdminRequest request) {
        return BusinessVerificationRequest.builder()
                .user(user)
                .userName(user.getName())
                .userRole(user.getUserRole())
                .userNickname(user.getNickname())
                .userEmail(userEmail)
                .businessNumber(request.getBusinessNumber())
                .businessOwnerName(request.getBusinessOwnerName())
                .businessRegistrationDate(request.getBusinessRegistrationDate())
                .businessName(request.getBusinessName())
                .inspectionStatus(InspectionStatus.PENDING)
                .build();
    }
}
