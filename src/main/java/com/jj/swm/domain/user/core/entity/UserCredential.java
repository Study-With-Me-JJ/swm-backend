package com.jj.swm.domain.user.core.entity;

import com.jj.swm.domain.user.core.dto.request.CreateUserRequest;
import com.jj.swm.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "user_credential")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCredential extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Provider provider;

    @Column(name = "value", nullable = false, length = 200)
    private String value;

    @Column(name = "login_id", unique = true)
    private String loginId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static UserCredential of(User user, CreateUserRequest createRequest) {
        return UserCredential.builder()
                .loginId(createRequest.getLoginId())
                .value(createRequest.getPassword())
                .user(user)
                .build();
    }

    public void modifyPassword(String encryptedPassword) {
        this.value = encryptedPassword;
    }
}
