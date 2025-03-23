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
import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import com.jj.swm.domain.studyroom.reservation.repository.StudyRoomReservationInfoRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
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
    public void createStudyRoomReservationAndSendSms() throws Exception{
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
}
