package com.jj.swm.domain.study.core.repository;

import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.repository.custom.CustomStudyRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyRepository extends JpaRepository<Study, Long>, CustomStudyRepository {

    Optional<Study> findByIdAndUserId(Long id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Study s where s.id = ?1")
    Optional<Study> findByIdUsingPessimisticLock(Long studyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Study s join fetch s.user where s.id = ?1")
    Optional<Study> findByIdWithUserUsingPessimisticLock(Long studyId);

    long countByIdInAndUserId(List<Long> studyIdList, UUID userId);

    @Modifying
    @Query("update Study s set s.deletedAt = CURRENT_TIMESTAMP where s.id in ?1")
    void deleteAllByStudyIdList(List<Long> studyIdList);

    @Query("select s.id from Study s where s.user.id = ?1")
    List<Long> findIdsByUserId(UUID userId);
}
