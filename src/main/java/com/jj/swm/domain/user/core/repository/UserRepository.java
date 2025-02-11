package com.jj.swm.domain.user.core.repository;

import com.jj.swm.domain.user.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByNickname(String nickname);
}
