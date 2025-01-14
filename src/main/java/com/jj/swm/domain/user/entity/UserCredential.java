package com.jj.swm.domain.user.entity;

import com.jj.swm.domain.user.dto.request.CreateUserRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Getter
@Entity
@Builder
@Table(name = "user_credential")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCredential {

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
