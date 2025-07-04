package com.jj.swm.domain.study.participation.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.core.support.RecruitmentPositionTestRepository;
import com.jj.swm.domain.study.core.support.StudyTestRepository;
import com.jj.swm.domain.study.participation.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.request.UpdateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.dto.response.UpdateStudyParticipationStatusResponse;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus;
import com.jj.swm.domain.study.participation.entity.StudyParticipationLink;
import com.jj.swm.domain.study.participation.fixture.dto.request.CreateStudyParticipationRequestFixture;
import com.jj.swm.domain.study.participation.fixture.dto.request.UpdateStudyParticipationRequestFixture;
import com.jj.swm.domain.study.participation.fixture.dto.request.UpdateStudyParticipationStatusRequestFixture;
import com.jj.swm.domain.study.participation.fixture.entity.StudyParticipationFixture;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.domain.study.participation.support.StudyParticipationLinkTestRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus.ACCEPTED;
import static com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus.REJECTED;
import static org.junit.jupiter.api.Assertions.*;

public class StudyParticipationCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    // target service
    @Autowired
    private StudyParticipationCommandService participationCommandService;

    // service
    @Autowired
    private StudyCommandService studyCommandService;

    //repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecruitmentPositionTestRepository recruitmentPositionRepository;

    @Autowired
    private StudyParticipationRepository participationRepository;

    @Autowired
    private StudyParticipationLinkRepository participationLinkRepository;

    @Autowired
    private StudyParticipationLinkTestRepository participationLinkTestRepository;

    @Autowired
    private StudyTestRepository studyRepository;

    // entity
    private User user1;
    private User user2;
    private StudyParticipation participation;
    private StudyRecruitmentPosition recruitmentPosition;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(UserFixture.create());
        user2 = userRepository.save(UserFixture.create());

        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user1.getId());
        Study study = studyRepository.findFirstByOrderByCreatedAt().orElseThrow();

        recruitmentPosition = recruitmentPositionRepository.findFirstByStudyId(study.getId()).orElseThrow();

        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPosition.getId(),
                user1.getId()
        );
        participation = participationRepository.findByStudyIdAndUserId(
                recruitmentPosition.getStudy().getId(), user1.getId()
        ).orElseThrow();
    }

    @Test
    @DisplayName("스터디 참여 생성에 성공한다.")
    void createStudyParticipation_Success() {
        //given
        CreateStudyParticipationRequest request = CreateStudyParticipationRequestFixture.create();

        //when
        participationCommandService.createStudyParticipation(
                request,
                recruitmentPosition.getId(),
                user2.getId()
        );

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findByStudyIdAndUserId(
                recruitmentPosition.getStudy().getId(), user2.getId()
        );
        assertTrue(optionalParticipation.isPresent());

        StudyParticipation participation = optionalParticipation.get();
        assertEquals(request.getKakaoId(), participation.getKakaoId());
        assertEquals(request.getCoverLetter(), participation.getCoverLetter());
        assertEquals(request.getFileInfo().getFileName(), participation.getFileInfo().getFileName());

        List<String> links = participationLinkRepository.findAllByParticipationId(participation.getId()).stream()
                .map(StudyParticipationLink::getLink).toList();
        assertTrue(links.containsAll(request.getLinks()));
    }

    @Test
    @DisplayName("links가 없어도 스터디 참여 생성에 성공한다.")
    void createStudyParticipation_WithoutLinks_Success() {
        //when
        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.createForNoLinkSuccess(),
                recruitmentPosition.getId(),
                user2.getId()
        );

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findByStudyIdAndUserId(
                recruitmentPosition.getStudy().getId(), user2.getId()
        );
        assertTrue(optionalParticipation.isPresent());
    }

    @Test
    @DisplayName("이미 스터디 참여를 생성했으면 실패한다.")
    void createStudyParticipation_WhenAlreadyExists_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPosition.getId(),
                user1.getId()
        ));
    }

    @Test
    @DisplayName("거절 상태가 아닌 참여 신청은 지우고 3일 이내에 참여를 생성해도 성공한다.")
    void createStudyParticipation_WhenDeletedNotRejectedParticipation_Success() {
        //given
        participationCommandService.deleteStudyParticipation(recruitmentPosition.getId(), user1.getId());

        //when
        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPosition.getId(),
                user1.getId()
        );

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findByStudyIdAndUserId(
                recruitmentPosition.getStudy().getId(), user1.getId()
        );
        assertTrue(optionalParticipation.isPresent());
    }

    @Test
    @DisplayName("거절 상태 참여 신청은 지우고 3일 이후에 참여를 생성하면 성공한다.")
    void createStudyParticipation_WhenDeletedRejectedParticipationAfterThreeDays_Success() {
        //given
        participationRepository.save(StudyParticipationFixture.createForDeletedAtAfterThreeDaysSuccess(
                recruitmentPosition.getStudy(),
                recruitmentPosition,
                user2
        ));

        //when
        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPosition.getId(),
                user2.getId()
        );

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findByStudyIdAndUserId(
                recruitmentPosition.getStudy().getId(), user2.getId()
        );
        assertTrue(optionalParticipation.isPresent());
    }

    @Test
    @DisplayName("거절 상태의 참여 신청을 지우고 3일이 지나기 전에 참여를 생성하면 실패한다.")
    void createStudyParticipation_WhenDeletedRejectedParticipationWithinThreeDays_ThenFail() {
        //given
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(REJECTED),
                participation.getId(),
                user1.getId()
        );
        participationCommandService.deleteStudyParticipation(recruitmentPosition.getId(), user1.getId());

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPosition.getId(),
                user1.getId()
        ));
    }

    @Test
    @DisplayName("스터디 참여 승인 상태 수정에 성공한다.")
    void updateStudyParticipationStatus_ToAcceptedStatus_Success() {
        //when
        StudyParticipationStatus newStatus = ACCEPTED;
        UpdateStudyParticipationStatusResponse response = participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(newStatus),
                participation.getId(),
                user1.getId()
        );

        //then
        StudyParticipation updatedParticipation = participationRepository.findById(participation.getId()).orElseThrow();

        assertEquals(response.getKakaoId(), updatedParticipation.getKakaoId());
        assertEquals(newStatus, updatedParticipation.getStatus());
    }

    @Test
    @DisplayName("스터디 참여 거절 상태 수정에 성공한다.")
    void updateStudyParticipationStatus_ToRejectedStatus_Success() {
        //when
        StudyParticipationStatus newStatus = REJECTED;
        UpdateStudyParticipationStatusResponse response = participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(newStatus),
                participation.getId(),
                user1.getId()
        );

        //then
        StudyParticipation updatedParticipation = participationRepository.findById(participation.getId()).orElseThrow();

        assertNull(response.getKakaoId());
        assertEquals(newStatus, updatedParticipation.getStatus());
    }

    @Test
    @DisplayName("승인 수가 모집 인원이랑 같으면 스터디 참여 승인 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenAcceptedCountEqualsHeadcount_ThenFail() {
        //given
        for (int i = 0; i < recruitmentPosition.getHeadcount(); i++) {
            User user = userRepository.save(UserFixture.create());
            participationCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    recruitmentPosition.getId(),
                    user.getId()
            );

            participationCommandService.updateStudyParticipationStatus(
                    UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                    participation.getId() + i,
                    user1.getId()
            );
        }

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                participation.getId() + recruitmentPosition.getHeadcount(),
                user1.getId()
        ));
    }

    @Test
    @DisplayName("스터디 모집 작성자가 아니면 스터디 참여 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenNotStudyWriter_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                participation.getId(),
                user2.getId()
        ));
    }

    @Test
    @DisplayName("기존 참여 상태가 대기 상태가 아니면 스터디 참여 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenStatusNotPending_ThenFail() {
        //given
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                participation.getId(),
                user1.getId()
        );

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(REJECTED),
                participation.getId(),
                user1.getId()
        ));
    }

    @Test
    @DisplayName("스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_Success() {
        //given
        UpdateStudyParticipationRequest request = UpdateStudyParticipationRequestFixture.create();

        long oldLinkSize = participationLinkTestRepository.countByParticipationId(participation.getId());

        //when
        participationCommandService.updateStudyParticipation(
                request,
                participation.getId(),
                user1.getId()
        );

        //then
        StudyParticipation updatedParticipation = participationRepository.findById(participation.getId()).orElseThrow();

        assertEquals(request.getKakaoId(), updatedParticipation.getKakaoId());
        assertEquals(request.getCoverLetter(), updatedParticipation.getCoverLetter());
        assertEquals(request.getFileInfo().getFileName(), updatedParticipation.getFileInfo().getFileName());

        assertEquals(
                oldLinkSize + request.getModifyLinkInfo().getLinksToAdd().size()
                        - request.getModifyLinkInfo().getLinkIdsToRemove().size(),
                participationLinkTestRepository.countByParticipationId(participation.getId())
        );
    }

    @Test
    @DisplayName("modifyLinkInfo가 null이어도 스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_ModifyLinkInfoNull_Success() {
        //given
        long oldLinkSize = participationLinkTestRepository.countByParticipationId(participation.getId());

        //when
        participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForModifyLinkInfoNullSuccess(),
                participation.getId(),
                user1.getId()
        );

        //then
        assertEquals(oldLinkSize, participationLinkTestRepository.countByParticipationId(participation.getId()));
    }

    @Test
    @DisplayName("linksToAdd&linkIdsToRemove가 null이어도 스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_LinksToAddNull_Success() {
        //given
        long oldLinkSize = participationLinkTestRepository.countByParticipationId(participation.getId());

        //when
        participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForLinksToAddNullSuccess(),
                participation.getId(),
                user1.getId()
        );

        //then
        assertEquals(oldLinkSize, participationLinkTestRepository.countByParticipationId(participation.getId()));
    }

    @Test
    @DisplayName("링크 개수 제한을 넘으면 스터디 참여 수정에 실패한다.")
    void updateStudyParticipation_WhenExceedLinkLimit_Success() {
        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForExceedLinkLimitFail(),
                participation.getId(),
                user1.getId()
        ));
    }

    @Test
    @DisplayName("tagIdsToRemove에 존재하지 않은 id값이 전달되면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenUnderTagLimit_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForWrongLinkIdToRemove(),
                participation.getId(),
                user1.getId()
        ));
    }

    @Test
    @DisplayName("이미 승인된 스터디 참여이면 수정에 실패한다.")
    void updateStudyParticipation_WhenAlreadyAccepted_Success() {
        //given
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                participation.getId(),
                user1.getId()
        );

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.create(),
                participation.getId(),
                user1.getId()
        ));
    }

    @Test
    @DisplayName("스터디 참여 삭제에 성공한다.")
    void deleteStudyParticipation_Success() {
        //when
        participationCommandService.deleteStudyParticipation(participation.getId(), user1.getId());

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(participation.getId());
        assertFalse(optionalParticipation.isPresent());

        assertEquals(0, participationLinkRepository.count());
    }

    @Test
    @DisplayName("스터디 참여의 모집 포지션 수정에 성공한다.")
    void updateStudyParticipationPosition_Success() {
        //given
        StudyRecruitmentPosition anotherRecruitmentPosition =
                recruitmentPositionRepository.findFirstByIdNot(recruitmentPosition.getStudy().getId()).orElseThrow();

        //when
        participationCommandService.updateStudyParticipationPosition(
                anotherRecruitmentPosition.getId(),
                participation.getId(),
                user1.getId()
        );

        //then
        StudyParticipation updatedParticipation = participationRepository.findById(participation.getId()).orElseThrow();
        assertEquals(anotherRecruitmentPosition.getId(), updatedParticipation.getRecruitmentPosition().getId());
    }

    @Test
    @DisplayName("동일하지 않은 스터디의 모집 포지션이면 스터디 참여 모집 포지션 수정에 실패한다.")
    void updateStudyParticipationPosition_WhenNewRecruitmentPositionIsAnotherStudy_ThenFail() {
        //given
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user1.getId());
        Study study = studyRepository.findFirstByOrderByCreatedAtDesc().orElseThrow();

        StudyRecruitmentPosition newRecruitmentPosition =
                recruitmentPositionRepository.findFirstByStudyId(study.getId()).orElseThrow();

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipationPosition(
                newRecruitmentPosition.getId(),
                participation.getId(),
                user1.getId()
        ));
    }
}
