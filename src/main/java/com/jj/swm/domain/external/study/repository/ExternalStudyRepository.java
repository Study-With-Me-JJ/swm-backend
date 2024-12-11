package com.jj.swm.domain.external.study.repository;

import com.jj.swm.domain.external.study.entity.ExternalStudy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExternalStudyRepository extends JpaRepository<ExternalStudy, String> {
    @Query("SELECT e FROM ExternalStudy e ORDER BY e.deadlineDate DESC")
    public Page<ExternalStudy> findAllExternalStudiesOrderByDeadlineDate(Pageable pageable);
}
