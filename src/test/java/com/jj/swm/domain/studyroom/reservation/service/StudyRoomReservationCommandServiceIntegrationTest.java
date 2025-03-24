package com.jj.swm.domain.studyroom.reservation.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.core.fixture.StudyRoomFixture;
import com.jj.swm.domain.studyroom.core.fixture.StudyRoomReserveTypeFixture;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomReserveTypeRepository;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationRequestEvent;
import com.jj.swm.domain.studyroom.reservation.dto.request.CreateStudyRoomReservationRequest;
import com.jj.swm.domain.studyroom.reservation.dto.request.UpdateStudyRoomReservationApprovalStatusRequest;
import com.jj.swm.domain.studyroom.reservation.dto.request.UpdateStudyRoomReservationRequest;
import com.jj.swm.domain.studyroom.reservation.entity.ApprovalStatus;
import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import com.jj.swm.domain.studyroom.reservation.fixture.StudyRoomReservationInfoFixture;
import com.jj.swm.domain.studyroom.reservation.repository.StudyRoomReservationInfoRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class StudyRoomReservationCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    // Target Service Bean
    @Autowired private StudyRoomReservationCommandService commandService;

    // Repository Bean
    @Autowired private StudyRoomReservationInfoRepository reservationInfoRepository;
    @Autowired private StudyRoomRepository studyRoomRepository;
    @Autowired private StudyRoomReserveTypeRepository reserveTypeRepository;
    @Autowired private UserRepository userRepository;

    private User createReservationUser;
    private StudyRoom studyRoom;
    private StudyRoomReserveType studyRoomReserveType;

    @BeforeEach
    public void setUp() {
        createReservationUser = userRepository.save(UserFixture.create());
        User roomAdmin = UserFixture.createRoomAdmin();
        userRepository.save(roomAdmin);
        studyRoom = studyRoomRepository.save(StudyRoomFixture.create(roomAdmin));
        studyRoomReserveType = reserveTypeRepository.save(StudyRoomReserveTypeFixture.create(studyRoom));
    }

    @Test
    @DisplayName("스터디 룸 예약 신청을 생성하고 카카오톡 알림을 전달한다.")
    public void createStudyRoomReservationAndSendSms_Success() throws Exception{
        //given
        LocalDateTime now = LocalDateTime.now();

        CreateStudyRoomReservationRequest request = CreateStudyRoomReservationRequest.builder()
                .reserverName("tester")
                .reserverPhoneNumber("010-0000-0000")
                .headcount(3)
                .checkInTime(now)
                .usageTime(3)
                .studyRoomReserveTypeId(studyRoomReserveType.getId())
                .build();

        given(kakaoNotificationService.sendStudyRoomReservationRequestNotification(
                any(StudyRoomReservationRequestEvent.class))).willReturn(CompletableFuture.completedFuture(true));

        //when
        commandService.createStudyRoomReservationAndSendSms(request, createReservationUser.getId());

        //then
        StudyRoomReservationInfo studyRoomReservationInfo = reservationInfoRepository.findById(1L).get();
        assertEquals("tester", studyRoomReservationInfo.getReserverName());
        assertEquals(3, studyRoomReservationInfo.getHeadcount());
        assertEquals(now.plusHours(3), studyRoomReservationInfo.getCheckOutTime());
        verify(kakaoNotificationService, times(1))
                .sendStudyRoomReservationRequestNotification(any(StudyRoomReservationRequestEvent.class));
    }

    @Test
    @DisplayName("스터디 룸 예약 신청을 승인한다.")
    public void updateStudyRoomReservationApprovalStatusAndSendSms_WhenApprove_Success() throws Exception{
        //given
        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo = reservationInfoRepository.save(reservationInfo);

        UpdateStudyRoomReservationApprovalStatusRequest request = UpdateStudyRoomReservationApprovalStatusRequest.builder()
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();

        //when
        commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, reservationInfo.getId());

        //then
        reservationInfo = reservationInfoRepository.findById(reservationInfo.getId()).get();
        assertEquals(ApprovalStatus.APPROVED, reservationInfo.getApprovalStatus());
    }

    @Test
    @DisplayName("스터디 룸 예약 신청을 거부한다.")
    public void updateStudyRoomReservationApprovalStatusAndSendSms_WhenRejected_Success() throws Exception{
        //given
        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo = reservationInfoRepository.save(reservationInfo);

        UpdateStudyRoomReservationApprovalStatusRequest request = UpdateStudyRoomReservationApprovalStatusRequest.builder()
                .approvalStatus(ApprovalStatus.REJECTED)
                .build();

        //when
        commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, reservationInfo.getId());

        //then
        reservationInfo = reservationInfoRepository.findById(reservationInfo.getId()).get();
        assertEquals(ApprovalStatus.REJECTED, reservationInfo.getApprovalStatus());
    }

    @Test
    @DisplayName("스터디 룸 예약 신청 수정에 성공한다.")
    public void updateStudyRoomReservation_Success() throws Exception{
        //given
        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo = reservationInfoRepository.save(reservationInfo);

        UpdateStudyRoomReservationRequest request = UpdateStudyRoomReservationRequest.builder()
                .reserverName("tester2")
                .reserverPhoneNumber("010-4567-8899")
                .headcount(2)
                .checkInTime(LocalDateTime.now())
                .usageTime(2)
                .build();

        //when
        commandService.updateStudyRoomReservation(request, reservationInfo.getId(), createReservationUser.getId());

        //then
        reservationInfo = reservationInfoRepository.findById(reservationInfo.getId()).get();
        assertEquals("tester2", reservationInfo.getReserverName());
        assertEquals("010-4567-8899", reservationInfo.getReserverPhoneNumber());
        assertEquals(2, reservationInfo.getHeadcount());
        assertEquals(2, reservationInfo.getUsageTime());
    }

    @Test
    @DisplayName("스터디 룸 예약 신청자가 아니라면 수정에 실패한다.")
    public void updateStudyRoomReservation_WhenNotReservationUser_ThenFail() throws Exception{
        //given
        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo = reservationInfoRepository.save(reservationInfo);

        UpdateStudyRoomReservationRequest request = UpdateStudyRoomReservationRequest.builder()
                .reserverName("tester2")
                .reserverPhoneNumber("010-4567-8899")
                .headcount(2)
                .checkInTime(LocalDateTime.now())
                .usageTime(2)
                .build();

        //when & then
        StudyRoomReservationInfo finalReservationInfo = reservationInfo;
        assertThrows(GlobalException.class, () -> commandService.updateStudyRoomReservation(
                request,
                finalReservationInfo.getId(),
                UUID.randomUUID())
        );
    }
}
