package com.jj.swm.domain.external.studyroom.repository;

import com.jj.swm.domain.common.KoreaRegion;
import com.jj.swm.domain.external.studyroom.entity.ExternalStudyRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalStudyRoomRepository extends JpaRepository<ExternalStudyRoom, Long> {

    Page<ExternalStudyRoom> findAllByKoreaRegion(Pageable pageable, KoreaRegion koreaRegion);
}
