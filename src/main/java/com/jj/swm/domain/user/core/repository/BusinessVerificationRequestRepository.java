package com.jj.swm.domain.user.core.repository;

import com.jj.swm.domain.user.core.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.core.entity.InspectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BusinessVerificationRequestRepository extends JpaRepository<BusinessVerificationRequest, Long> {

    boolean existsByBusinessNumber(String businessNumber);

    @Query("select bvr from BusinessVerificationRequest bvr where bvr.inspectionStatus in ?1")
    Page<BusinessVerificationRequest> findPagedVerificationRequestWithStatus(List<InspectionStatus> status, Pageable pageable);

    @Modifying
    @Query("update BusinessVerificationRequest bvr set bvr.inspectionStatus = ?2 where bvr.id in ?1")
    void updateInspectionStatus(List<Long> businessVerificationRequestIds, InspectionStatus status);

    @Modifying
    @Query("delete from BusinessVerificationRequest bvr where bvr.user.id = ?1")
    void deleteAllByUserId(UUID userId);
}
