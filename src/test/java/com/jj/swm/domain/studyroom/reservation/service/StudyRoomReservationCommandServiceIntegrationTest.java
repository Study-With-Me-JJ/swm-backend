package com.jj.swm.domain.studyroom.reservation.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.core.fixture.StudyRoomFixture;
import com.jj.swm.domain.studyroom.core.fixture.StudyRoomReserveTypeFixture;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomReserveTypeRepository;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationRequestEvent;
import com.jj.swm.domain.studyroom.reservation.dto.event.StudyRoomReservationResponseEvent;
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
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.enums.ExpirationTime;
import com.jj.swm.global.common.enums.RedisPrefix;
import com.jj.swm.global.exception.GlobalException;
import com.jj.swm.global.security.jwt.JwtProvider;
import com.jj.swm.global.security.jwt.TokenRedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class StudyRoomReservationCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    // Target Service Bean
    @Autowired private StudyRoomReservationCommandService commandService;

    // Service Bean
    @Autowired private TokenRedisService tokenRedisService;
    @Autowired private JwtProvider jwtProvider;

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
        given(kakaoNotificationService.sendStudyRoomReservationRequestNotification(
                any(StudyRoomReservationRequestEvent.class), any(String.class))).willReturn(CompletableFuture.completedFuture(true));

        LocalDateTime now = LocalDateTime.now();

        CreateStudyRoomReservationRequest request = CreateStudyRoomReservationRequest.builder()
                .reserverName("tester")
                .reserverPhoneNumber("010-0000-0000")
                .headcount(3)
                .checkInTime(now)
                .usageTime(3)
                .studyRoomReserveTypeId(studyRoomReserveType.getId())
                .build();

        //when
        commandService.createStudyRoomReservationAndSendSms(request, createReservationUser.getId());

        //then
        StudyRoomReservationInfo studyRoomReservationInfo = reservationInfoRepository.findById(1L).get();
        assertEquals("tester", studyRoomReservationInfo.getReserverName());
        assertEquals(3, studyRoomReservationInfo.getHeadcount());
        verify(kakaoNotificationService, times(1))
                .sendStudyRoomReservationRequestNotification(any(StudyRoomReservationRequestEvent.class), any(String.class));
    }

    @Test
    @DisplayName("예약 토큰으로 스터디 룸 예약 신청을 승인한다.")
    public void updateStudyRoomReservationApprovalStatusAndSendSms_WhenApproveWithReservationToken_Success() throws Exception{
        //given
        given(kakaoNotificationService.sendStudyRoomReservationResponseNotification(
                any(StudyRoomReservationResponseEvent.class))).willReturn(CompletableFuture.completedFuture(true));

        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo = reservationInfoRepository.save(reservationInfo);

        UpdateStudyRoomReservationApprovalStatusRequest request = UpdateStudyRoomReservationApprovalStatusRequest.builder()
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();

        String reservationToken = insertReservationToken(reservationInfo.getId());

        //when
        commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, reservationToken);

        //then
        reservationInfo = reservationInfoRepository.findById(reservationInfo.getId()).get();
        assertEquals(ApprovalStatus.APPROVED, reservationInfo.getApprovalStatus());
        assertThrows(GlobalException.class,
                () -> tokenRedisService.findReservationIdByReservationTokenOrThrow(reservationToken)
        );
    }

    @Test
    @DisplayName("예약 토큰으로 스터디 룸 예약 신청을 거부한다.")
    public void updateStudyRoomReservationApprovalStatusAndSendSms_WhenRejectWithReservationToken_Success() throws Exception{
        //given
        given(kakaoNotificationService.sendStudyRoomReservationResponseNotification(
                any(StudyRoomReservationResponseEvent.class))).willReturn(CompletableFuture.completedFuture(true));

        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo = reservationInfoRepository.save(reservationInfo);

        UpdateStudyRoomReservationApprovalStatusRequest request = UpdateStudyRoomReservationApprovalStatusRequest.builder()
                .approvalStatus(ApprovalStatus.REJECTED)
                .build();

        String reservationToken = insertReservationToken(reservationInfo.getId());

        //when
        commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, reservationToken);

        //then
        reservationInfo = reservationInfoRepository.findById(reservationInfo.getId()).get();
        assertEquals(ApprovalStatus.REJECTED, reservationInfo.getApprovalStatus());
        assertThrows(GlobalException.class,
                () -> tokenRedisService.findReservationIdByReservationTokenOrThrow(reservationToken)
        );
    }

    @Test
    @DisplayName("저장된 예약 토큰 값이 없다면 스터디 룸 승인/거부에 실패한다.")
    public void updateStudyRoomReservationApprovalStatusAndSendSms_WhenNotFoundReservationToken_ThenFail() throws Exception{
        //given
        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfoRepository.save(reservationInfo);

        UpdateStudyRoomReservationApprovalStatusRequest request = UpdateStudyRoomReservationApprovalStatusRequest.builder()
                .approvalStatus(ApprovalStatus.REJECTED)
                .build();

        //when & then
        assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, "emptyToken")
        );
    }

    @Test
    @DisplayName("예약 정보 ID로 스터디 룸 예약 신청을 승인한다.")
    public void updateStudyRoomReservationApprovalStatusAndSendSms_WhenApproveWithReservationInfoId_Success() throws Exception{
        //given
        given(kakaoNotificationService.sendStudyRoomReservationResponseNotification(
                any(StudyRoomReservationResponseEvent.class))).willReturn(CompletableFuture.completedFuture(true));

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
    @DisplayName("예약 정보 ID로 스터디 룸 예약 신청을 거부한다.")
    public void updateStudyRoomReservationApprovalStatusAndSendSms_WhenRejectWithReservationInfoId_Success() throws Exception{
        //given
        given(kakaoNotificationService.sendStudyRoomReservationResponseNotification(
                any(StudyRoomReservationResponseEvent.class))).willReturn(CompletableFuture.completedFuture(true));

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
    @DisplayName("스터디 룸 예약 정보가 존재하지 않는다면, 예약 신청 승인/거부에 실패한다.")
    public void updateStudyRoomReservationApprovalStatusAndSendSms_WhenNotFoundReservationInfo_ThenFail() throws Exception{
        //given
        UpdateStudyRoomReservationApprovalStatusRequest request = UpdateStudyRoomReservationApprovalStatusRequest.builder()
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();

        //when & then
        assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, 100L)
        );
    }

    @Test
    @DisplayName("스터디 룸 예약 승인 상태 값이 CANCELED라면 승인/거부에 실패한다.")
    public void updateStudyRoomReservationApprovalStatusAndSendSms_WhenStatusCanceled_ThenFail() throws Exception{
        //given
        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo.modifyApprovalStatus(ApprovalStatus.CANCELED);

        reservationInfo = reservationInfoRepository.save(reservationInfo);

        UpdateStudyRoomReservationApprovalStatusRequest request = UpdateStudyRoomReservationApprovalStatusRequest.builder()
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();

        String reservationToken = insertReservationToken(reservationInfo.getId());

        //when & then
        assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomReservationApprovalStatusAndSendSms(request, reservationToken)
        );
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

    @Test
    @DisplayName("스터디 룸 예약 신청이 승인/거부 상태라면 수정에 실패한다.")
    public void updateStudyRoomReservation_WhenStatusApprovedOrRejected_ThenFail() throws Exception{
        //given
        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo.modifyApprovalStatus(ApprovalStatus.APPROVED);

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
                createReservationUser.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 예약 신청 취소에 성공한다.")
    public void cancelStudyRoomReservation_Success() throws Exception{
        //given
        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo = reservationInfoRepository.save(reservationInfo);

        //when
        commandService.cancelStudyRoomReservation(reservationInfo.getId(), createReservationUser.getId());

        //then
        reservationInfo = reservationInfoRepository.findById(reservationInfo.getId()).get();
        assertEquals(ApprovalStatus.CANCELED, reservationInfo.getApprovalStatus());
    }

    @Test
    @DisplayName("스터디 룸 예약 신청이 이미 승인/거부 되었다면, 취소에 실패한다.")
    public void cancelStudyRoomReservation_WhenStatusApprovedOrRejected_ThenFail() throws Exception{
        //given
        StudyRoomReservationInfo reservationInfo = StudyRoomReservationInfoFixture.create(
                createReservationUser,
                studyRoom,
                studyRoomReserveType
        );

        reservationInfo.modifyApprovalStatus(ApprovalStatus.APPROVED);

        reservationInfo = reservationInfoRepository.save(reservationInfo);

        //when & then
        StudyRoomReservationInfo finalReservationInfo = reservationInfo;
        assertThrows(GlobalException.class, () -> commandService.cancelStudyRoomReservation(
                finalReservationInfo.getId(),
                createReservationUser.getId())
        );
    }

    private String insertReservationToken(Long reservationId) {
        String reservationToken = jwtProvider.generateTokenForReservation(
                reservationId, ExpirationTime.STUDYROOM_RESERVATION_TOKEN.getValue()
        );

        tokenRedisService.saveReservationToken(
                RedisPrefix.STUDYROOM_RESERVATION_TOKEN.getValue() + reservationToken,
                reservationId.toString()
        );

        return reservationToken;
    }
}
