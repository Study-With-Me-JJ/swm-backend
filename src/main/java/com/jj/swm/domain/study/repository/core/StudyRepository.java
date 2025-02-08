package com.jj.swm.domain.study.repository.core;

import com.jj.swm.domain.study.entity.core.Study;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StudyRepository extends JpaRepository<Study, Long>, CustomStudyRepository {

    Optional<Study> findByIdAndUserId(Long id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Study s join fetch s.user where s.id = ?1")
    Optional<Study> findByIdWithUserUsingPessimisticLock(Long studyId);
}
