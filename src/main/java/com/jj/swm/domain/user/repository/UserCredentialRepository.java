package com.jj.swm.domain.user.repository;

import com.jj.swm.domain.user.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    boolean existsByLoginId(String loginId);

    Optional<UserCredential> findByLoginId(String loginId);

    @Query("select uc from UserCredential uc join fetch uc.user u where uc.value = ?1")
    Optional<UserCredential> findByValue(String value);
}
