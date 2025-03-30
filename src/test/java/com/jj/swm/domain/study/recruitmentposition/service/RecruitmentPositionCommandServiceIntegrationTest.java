package com.jj.swm.domain.study.recruitmentposition.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateStudyParticipationRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpsertRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.response.UpdateStudyParticipationStatusResponse;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationLink;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipationStatus;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.recruitmentposition.fixture.dto.request.CreateStudyParticipationRequestFixture;
import com.jj.swm.domain.study.recruitmentposition.fixture.dto.request.UpdateStudyParticipationRequestFixture;
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

    @Test
    @DisplayName("이미 모집 인원 수만큼 승인 수가 채워졌으면 참여 생성에 실패한다.")
    void createStudyParticipation_WhenAcceptedCountEqualHeadcount_ThenFail() {
        //given
        for (int i = 0; i <= 2; i++) {
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
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("승인 수보다 모집 인원 수를 더 작게 변경하면 모집 포지션 수정에 실패한다.")
    void updateRecruitmentPosition_WhenAcceptedCountLessThanHeadcount_ThenFail() {
        //given
        recruitmentPositionCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user.getId()
        );

        Long newParticipationId = 2L;

        recruitmentPositionCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.ACCEPTED),
                participationId,
                user.getId()
        );

        recruitmentPositionCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.ACCEPTED),
                newParticipationId,
                user.getId()
        );

        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.updateRecruitmentPosition(
                UpsertRecruitmentPositionRequestFixture.updateForAcceptedCountLessThanHeadcountFail(),
                recruitmentPositionId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_Success() {
        //given
        UpdateStudyParticipationRequest request = UpdateStudyParticipationRequestFixture.create();

        //when
        recruitmentPositionCommandService.updateStudyParticipation(
                request,
                participationId,
                user.getId()
        );

        //then
        StudyParticipation participation = participationRepository.findById(participationId).get();

        assertEquals(request.getKakaoId(), participation.getKakaoId());
        assertEquals(request.getFileInfo().getFileName(), participation.getFileInfo().getFileName());

        Optional<StudyParticipationLink> optionalLink = participationLinkRepository.findById(3L); // 기존 2개에서 추가이므로 3부터 시작
        assertTrue(optionalLink.isPresent());

        optionalLink = participationLinkRepository.findById(request.getModifyLinkRequest()
                .getLinkIdsToRemove()
                .getFirst());
        assertFalse(optionalLink.isPresent());

        assertEquals(2, participationLinkRepository.countByParticipationId(participationId)); // 기존 2개에서 2개 추가하고 2개 제거
    }

    @Test
    @DisplayName("link 수정 객체가 null이어도 스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_ModifyLinkRequestNull_Success() {
        //when
        recruitmentPositionCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForModifyLinkRequestNullSuccess(),
                participationId,
                user.getId()
        );

        //then
        Optional<StudyParticipationLink> optionalLink = participationLinkRepository.findById(3L); // 기존 2개에서 추가이므로 3부터 시작
        assertFalse(optionalLink.isPresent());

        optionalLink = participationLinkRepository.findById(1L);
        assertTrue(optionalLink.isPresent());

        assertEquals(2, participationLinkRepository.countByParticipationId(participationId)); // 기존 2개에서 수정 없음
    }

    @Test
    @DisplayName("linksToAdd가 null이어도 스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_LinksToAddNull_Success() {
        //when
        recruitmentPositionCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForLinksToAddNullSuccess(),
                participationId,
                user.getId()
        );

        //then
        assertEquals(0, participationLinkRepository.countByParticipationId(participationId)); // 기존 2개에서 추가 없이 2개 제거
    }

    @Test
    @DisplayName("linkIdsToRemove가 null이어도 스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_LinkIdsToRemoveNull_Success() {
        //when
        recruitmentPositionCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForLinkIdsToRemoveNullSuccess(),
                participationId,
                user.getId()
        );

        //then
        assertEquals(3, participationLinkRepository.countByParticipationId(participationId)); // 기존 2개에서 제거 없이 1개 추가
    }

    @Test
    @DisplayName("링크 개수 제한을 넘으면 스터디 참여 수정에 실패한다.")
    void updateStudyParticipation_WhenExceedLinkLimit_Success() {
        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForExceedLinkLimitFail(),
                participationId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("링크 개수가 0 미만이면 스터디 참여 수정에 실패한다.")
    void updateStudyParticipation_WhenUnderLinkLimit_Success() {
        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForUnderLinkLimitFail(),
                participationId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("이미 승인된 스터디 참여이면 수정에 실패한다.")
    void updateStudyParticipation_WhenAlreadyAccepted_Success() {
        //given
        recruitmentPositionCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.ACCEPTED),
                participationId,
                user.getId()
        );

        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.create(),
                participationId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("스터디 참여 삭제에 성공한다.")
    void deleteStudyParticipation_Success() {
        //when
        recruitmentPositionCommandService.deleteStudyParticipation(participationId, user.getId());

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(participationId);
        assertFalse(optionalParticipation.isPresent());
    }

    @Test
    @DisplayName("이미 승인된 스터디 참여이면 삭제에 실패한다.")
    void deleteStudyParticipation_WhenAlreadyAccepted_ThenFail() {
        //given
        recruitmentPositionCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(StudyParticipationStatus.ACCEPTED),
                participationId,
                user.getId()
        );

        //when & then
        assertThrows(
                GlobalException.class,
                () -> recruitmentPositionCommandService.deleteStudyParticipation(participationId, user.getId())
        );
    }
}
