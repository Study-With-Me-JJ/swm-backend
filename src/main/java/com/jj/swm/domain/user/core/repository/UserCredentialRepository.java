package com.jj.swm.domain.user.core.repository;

import com.jj.swm.domain.user.core.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    boolean existsByLoginId(String loginId);

    @Query("select uc from UserCredential uc join fetch uc.user u where uc.loginId = ?1")
    Optional<UserCredential> findByLoginId(String loginId);

    @Query("select uc from UserCredential uc join fetch uc.user u where uc.value = ?1")
    Optional<UserCredential> findByValue(String value);

    @Query("select count(uc) > 0 from UserCredential uc join uc.user u where uc.loginId = ?1 and u.name = ?2")
    boolean existsByLoginIdAndName(String loginId, String name);

    @Modifying
    @Query("update UserCredential uc set uc.deletedAt = CURRENT_TIMESTAMP where uc.user.id = ?1")
    void deleteAllByUserId(UUID userId);

    @Query(value = """
            SELECT * FROM user_credential uc 
            WHERE uc.user_id = ?1 
            ORDER BY uc.created_at DESC LIMIT 1
    """, nativeQuery = true)
    Optional<UserCredential> findRecentByUserId(UUID userId);
}
