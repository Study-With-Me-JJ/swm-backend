package com.jj.swm.domain.user.repository;

import com.jj.swm.domain.user.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    boolean existsByLoginId(String loginId);
}
