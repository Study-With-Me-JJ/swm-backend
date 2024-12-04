package com.jj.swm.domain.user.repository;

import com.jj.swm.domain.user.entity.RoleType;
import com.jj.swm.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByIdAndUserRole(UUID userId, RoleType roleType);

}
