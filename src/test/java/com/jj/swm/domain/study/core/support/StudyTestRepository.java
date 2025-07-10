package com.jj.swm.domain.study.core.support;

import com.jj.swm.domain.study.core.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
public interface StudyTestRepository extends JpaRepository<Study, Long> {

    Optional<Study> findFirstByOrderById();

    Optional<Study> findFirstByOrderByIdDesc();
}
