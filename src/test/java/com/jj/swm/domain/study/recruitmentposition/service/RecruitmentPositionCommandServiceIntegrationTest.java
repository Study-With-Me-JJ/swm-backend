package com.jj.swm.domain.study.recruitmentposition.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpsertRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.response.UpdateStudyParticipationStatusResponse;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationStatus;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.recruitmentposition.fixture.dto.request.CreateStudyParticipationRequestFixture;
import com.jj.swm.domain.study.recruitmentposition.fixture.dto.request.UpdateStudyParticipationStatusRequestFixture;
import com.jj.swm.domain.study.recruitmentposition.fixture.dto.request.UpsertRecruitmentPositionRequestFixture;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.recruitmentposition.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.recruitmentposition.repository.StudyParticipationRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RecruitmentPositionCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    // target service
    @Autowired
    private RecruitmentPositionCommandService recruitmentPositionCommandService;

    // service
    @Autowired
    private StudyCommandService studyCommandService;

    //repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecruitmentPositionRepository recruitmentPositionRepository;

    @Autowired
    private StudyParticipationRepository participationRepository;

    @Autowired
    private StudyParticipationLinkRepository participationLinkRepository;

    // entity
    private User user;
    private final Long studyId = 1L;
    private final Long participationId = 1L;
    private final Long recruitmentPositionId = 1L; // addStudy 할 시에 모집 포지션 2개 삽입

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        recruitmentPositionCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user.getId()
        );
    }

    @Test
    @DisplayName("모집 포지션 생성에 성공한다.")
    void createRecruitmentPosition_Success() {
        //given
        UpsertRecruitmentPositionRequest request = UpsertRecruitmentPositionRequestFixture.create();

        //when
        Long newRecruitmentPositionId = recruitmentPositionCommandService.createRecruitmentPosition(
                request,
                studyId,
                user.getId()
        ).getRecruitmentPositionId();

        //then
        Optional<StudyRecruitmentPosition> optionalRecruitmentPosition =
                recruitmentPositionRepository.findById(newRecruitmentPositionId);
        assertTrue(optionalRecruitmentPosition.isPresent());

        StudyRecruitmentPosition recruitmentPosition = optionalRecruitmentPosition.get();
        assertEquals(request.getHeadcount(), recruitmentPosition.getHeadcount());
    }

    @Test
    @DisplayName("모집 포지션 최대 개수에 도달하면 생성에 실패한다.")
    void createRecruitmentPosition_WhenExceedLimit_ThenFail() {
        //given
        UpsertRecruitmentPositionRequest request = UpsertRecruitmentPositionRequestFixture.create();

        for (int i = 1; i <= 8; i++) {
            recruitmentPositionCommandService.createRecruitmentPosition(
                    request,
                    studyId,
                    user.getId()
            );
        }

        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.createRecruitmentPosition(
                request,
                studyId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("모집 포지션 수정에 성공한다.")
    void updateRecruitmentPosition_Success() {
        //given
        UpsertRecruitmentPositionRequest request = UpsertRecruitmentPositionRequestFixture.update();

        //when
        recruitmentPositionCommandService.updateRecruitmentPosition(
                request,
                recruitmentPositionId,
                user.getId()
        );

        //then
        StudyRecruitmentPosition recruitmentPosition =
                recruitmentPositionRepository.findById(recruitmentPositionId).get();

        assertEquals(request.getHeadcount(), recruitmentPosition.getHeadcount());
        assertEquals(request.getTitle(), recruitmentPosition.getTitle());
    }

    @Test
    @DisplayName("모집 포지션 삭제에 성공한다.")
    void deleteRecruitmentPosition_Success() {
        //when
        recruitmentPositionCommandService.deleteRecruitmentPosition(recruitmentPositionId, user.getId());

        //then
        assertEquals(1, recruitmentPositionRepository.count());
    }

    @Test
    @DisplayName("존재하지 않는 모집 포지션이면 삭제에 실패한다.")
    void deleteRecruitmentPosition_WhenNonExists_ThenFail() {
        //when & then
        assertThrows(
                GlobalException.class,
                () -> recruitmentPositionCommandService.deleteRecruitmentPosition(123456789L, user.getId()));
    }

    @Test
    @DisplayName("스터디 참여 생성에 성공한다.")
    void createStudyParticipation_Success() {
        //given
        CreateStudyParticipationRequest request = CreateStudyParticipationRequestFixture.create();

        //when
        recruitmentPositionCommandService.createStudyParticipation(
                request,
                recruitmentPositionId,
                user.getId()
        );
        Long newParticipationId = 2L;

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(newParticipationId);
        assertTrue(optionalParticipation.isPresent());

        StudyParticipation participation = optionalParticipation.get();
        assertEquals(request.getCoverLetter(), participation.getCoverLetter());

        assertEquals(request.getLinks().size(), participationLinkRepository.count() / 2); // setUp에 의해 /2 진행
    }

    @Test
    @DisplayName("links&fileUrls가 없어도 스터디 참여 생성에 성공한다.")
    void createStudyParticipation_WithoutLinksAndFileUrls_Success() {
        //when
        recruitmentPositionCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.createForNoLinkAndFileUrlSuccess(),
                recruitmentPositionId,
                user.getId()
        );
        Long newParticipationId = 2L;

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(newParticipationId);
        assertTrue(optionalParticipation.isPresent());
    }

    @Test
    @DisplayName("스터디 참여 승인 상태 수정에 성공한다.")
    void updateStudyParticipationStatus_ToAcceptedStatus_Success() {
        //when
        UpdateStudyParticipationStatusResponse response =
                recruitmentPositionCommandService.updateStudyParticipationStatus(
                        UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.ACCEPTED),
                        participationId,
                        user.getId()
                );

        //then
        StudyParticipation participation = participationRepository.findById(participationId).get();

        assertEquals(response.getKakaoId(), participation.getKakaoId());
        assertEquals(StudyParticipationStatus.ACCEPTED, participation.getStatus());
    }

    @Test
    @DisplayName("스터디 참여 거절 상태 수정에 성공한다.")
    void updateStudyParticipationStatus_ToRejectedStatus_Success() {
        //when
        UpdateStudyParticipationStatusResponse response =
                recruitmentPositionCommandService.updateStudyParticipationStatus(
                        UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.REJECTED),
                        participationId,
                        user.getId()
                );

        //then
        StudyParticipation participation = participationRepository.findById(participationId).get();

        assertNull(response);
        assertEquals(StudyParticipationStatus.REJECTED, participation.getStatus());
    }

    @Test
    @DisplayName("승인 수가 모집 인원이랑 같으면 스터디 참여 승인 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenAcceptedCountEqualsHeadcount_ThenFail() {
        //given
        for (int i = 1; i <= 3; i++) {
            recruitmentPositionCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    recruitmentPositionId,
                    user.getId()
            );

            Long newParticipationId = participationId + i;

            recruitmentPositionCommandService.updateStudyParticipationStatus(
                    UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.ACCEPTED),
                    newParticipationId,
                    user.getId()
            );
        }

        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.ACCEPTED),
                participationId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("스터디 작성자가 아니면 스터디 참여 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenNotStudyWriter_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.ACCEPTED),
                participationId,
                UserFixture.uuid
        ));
    }

    @Test
    @DisplayName("기존 참여 상태가 대기가 아니면 스터디 참여 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenStatusNotPending_ThenFail() {
        //given
        recruitmentPositionCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.ACCEPTED),
                participationId,
                user.getId()
        );

        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.REJECTED),
                participationId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("수정할 상태가 대기 상태면 스터디 참여 승인 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenNewStatusPending_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.PENDING),
                participationId,
                user.getId()
        ));
    }
}
