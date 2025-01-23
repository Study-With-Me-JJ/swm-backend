package com.jj.swm.domain.user.repository;

import com.jj.swm.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByNickname(String nickname);
}
