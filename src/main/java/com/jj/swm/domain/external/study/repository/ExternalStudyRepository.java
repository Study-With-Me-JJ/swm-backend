package com.jj.swm.domain.external.study.repository;

import com.jj.swm.domain.external.study.entity.ExternalStudy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalStudyRepository extends JpaRepository<ExternalStudy, String> {

}
