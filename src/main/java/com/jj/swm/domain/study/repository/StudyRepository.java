package com.jj.swm.domain.study.repository;

import com.jj.swm.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StudyRepository extends JpaRepository<Study, Long>, CustomStudyRepository {

    @Query("select s from Study s join fetch s.user where s.id = ?1 and s.user.id = ?2")
    Optional<Study> findWithUserByIdAndUserId(Long id, UUID userId);
}
