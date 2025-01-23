package com.jj.swm.domain.user.repository;

import com.jj.swm.domain.user.entity.BusinessVerificationRequest;
import com.jj.swm.domain.user.entity.InspectionStatus;
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
    @Query("update BusinessVerificationRequest bvr set bvr.inspectionStatus = 'APPROVED' where bvr.id in ?1")
    void updateInspectionStatusApproval(List<Long> businessVerificationRequestIds);

    @Modifying
    @Query("update BusinessVerificationRequest bvr set bvr.inspectionStatus = 'REJECTED' where bvr.id in ?1")
    void updateInspectionStatusRejection(List<Long> businessVerificationRequestIds);

    long countByIdIn(List<Long> ids);

    @Modifying
    @Query("delete from BusinessVerificationRequest bvr where bvr.user.id = ?1")
    void deleteAllByUserId(UUID userId);
}
