package com.jj.swm.domain.study.participation.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.core.service.StudyCommandService;
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

import static com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus.*;
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
    private RecruitmentPositionRepository recruitmentPositionRepository;

    @Autowired
    private StudyParticipationRepository participationRepository;

    @Autowired
    private StudyParticipationLinkRepository participationLinkRepository;

    @Autowired
    private StudyParticipationLinkTestRepository participationLinkTestRepository;

    @Autowired
    private StudyRepository studyRepository;

    // entity
    private User user1;
    private User user2;
    private final int headcount = 3; //addStudy 할 시에 모집 포지션의 headcount
    private final Long participationId = 1L;
    private final Long recruitmentPositionId = 1L; // addStudy 할 시에 모집 포지션 4개 삽입

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(UserFixture.create());
        user2 = userRepository.save(UserFixture.create());
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user1.getId());
        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user1.getId()
        );
    }

    @Test
    @DisplayName("스터디 참여 생성에 성공한다.")
    void createStudyParticipation_Success() {
        //given
        CreateStudyParticipationRequest request = CreateStudyParticipationRequestFixture.create();

        //when
        participationCommandService.createStudyParticipation(
                request,
                recruitmentPositionId,
                user2.getId()
        );
        Long newParticipationId = 2L;

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(newParticipationId);
        assertTrue(optionalParticipation.isPresent());

        StudyParticipation participation = optionalParticipation.get();
        assertEquals(request.getKakaoId(), participation.getKakaoId());
        assertEquals(request.getCoverLetter(), participation.getCoverLetter());
        assertEquals(request.getFileInfo().getFileName(), participation.getFileInfo().getFileName());

        List<String> links = participationLinkRepository.findAllByParticipationId(newParticipationId).stream()
                .map(StudyParticipationLink::getLink).toList();
        assertTrue(links.containsAll(request.getLinks()));
    }

    @Test
    @DisplayName("links가 없어도 스터디 참여 생성에 성공한다.")
    void createStudyParticipation_WithoutLinks_Success() {
        //when
        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.createForNoLinkSuccess(),
                recruitmentPositionId,
                user2.getId()
        );
        Long newParticipationId = 2L;

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(newParticipationId);
        assertTrue(optionalParticipation.isPresent());
    }

    @Test
    @DisplayName("이미 스터디 참여를 생성했으면 실패한다.")
    void createStudyParticipation_WhenAlreadyExists_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user1.getId()
        ));
    }

    @Test
    @DisplayName("거절 상태가 아닌 참여 신청은 지우고 3일 이내에 참여를 생성해도 성공한다.")
    void createStudyParticipation_WhenDeletedNotRejectedParticipation_Success() {
        //given
        participationCommandService.deleteStudyParticipation(recruitmentPositionId, user1.getId());

        //when
        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user1.getId()
        );
        Long newParticipationId = 2L;

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(newParticipationId);
        assertTrue(optionalParticipation.isPresent());
    }

    @Test
    @DisplayName("거절 상태 참여 신청은 지우고 3일 이후에 참여를 생성하면 성공한다.")
    void createStudyParticipation_WhenDeletedRejectedParticipationAfterThreeDays_Success() {
        //given
        Study study = studyRepository.getReferenceById(1L); //setUp에서 생성된 스터디의 id
        StudyRecruitmentPosition recruitmentPosition =
                recruitmentPositionRepository.getReferenceById(recruitmentPositionId);
        participationRepository.save(StudyParticipationFixture.createForDeletedAtAfterThreeDaysSuccess(
                study,
                recruitmentPosition,
                user2
        ));

        //when & then
        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user2.getId()
        );
        Long newParticipationId = 3L; // 위에서 하나 들어갔으므로 id가 3일 차례

        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(newParticipationId);
        assertTrue(optionalParticipation.isPresent());
    }

    @Test
    @DisplayName("거절 상태의 참여 신청을 지우고 3일이 지나기 전에 참여를 생성하면 실패한다.")
    void createStudyParticipation_WhenDeletedRejectedParticipationWithinThreeDays_ThenFail() {
        //given
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(REJECTED),
                participationId,
                user1.getId()
        );
        participationCommandService.deleteStudyParticipation(recruitmentPositionId, user1.getId());

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
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
                participationId,
                user1.getId()
        );

        //then
        StudyParticipation participation = participationRepository.findById(participationId).orElseThrow();

        assertEquals(response.getKakaoId(), participation.getKakaoId());
        assertEquals(newStatus, participation.getStatus());
    }

    @Test
    @DisplayName("스터디 참여 거절 상태 수정에 성공한다.")
    void updateStudyParticipationStatus_ToRejectedStatus_Success() {
        //when
        StudyParticipationStatus newStatus = REJECTED;
        UpdateStudyParticipationStatusResponse response = participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(newStatus),
                participationId,
                user1.getId()
        );

        //then
        StudyParticipation participation = participationRepository.findById(participationId).orElseThrow();

        assertNull(response.getKakaoId());
        assertEquals(newStatus, participation.getStatus());
    }

    @Test
    @DisplayName("승인 수가 모집 인원이랑 같으면 스터디 참여 승인 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenAcceptedCountEqualsHeadcount_ThenFail() {
        //given
        for (int i = 0; i < headcount; i++) {
            User user = userRepository.save(UserFixture.create());
            participationCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    recruitmentPositionId,
                    user.getId()
            );

            participationCommandService.updateStudyParticipationStatus(
                    UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                    participationId + i,
                    user1.getId()
            );
        }

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                participationId + headcount,
                user1.getId()
        ));
    }

    @Test
    @DisplayName("스터디 모집 작성자가 아니면 스터디 참여 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenNotStudyWriter_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                participationId,
                user2.getId()
        ));
    }

    @Test
    @DisplayName("기존 참여 상태가 대기 상태가 아니면 스터디 참여 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenStatusNotPending_ThenFail() {
        //given
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                participationId,
                user1.getId()
        );

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(REJECTED),
                participationId,
                user1.getId()
        ));
    }

    @Test
    @DisplayName("수정할 상태가 대기 상태면 스터디 참여 승인 상태 수정에 실패한다.")
    void updateStudyParticipationStatus_WhenNewStatusPending_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(PENDING),
                participationId,
                user1.getId()
        ));
    }

    @Test
    @DisplayName("스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_Success() {
        //given
        UpdateStudyParticipationRequest request = UpdateStudyParticipationRequestFixture.create();

        //when
        participationCommandService.updateStudyParticipation(
                request,
                participationId,
                user1.getId()
        );

        //then
        StudyParticipation participation = participationRepository.findById(participationId).orElseThrow();

        assertEquals(request.getKakaoId(), participation.getKakaoId());
        assertEquals(request.getCoverLetter(), participation.getCoverLetter());
        assertEquals(request.getFileInfo().getFileName(), participation.getFileInfo().getFileName());

        Optional<StudyParticipationLink> optionalLink = participationLinkRepository.findById(3L); // 기존 2개에서 추가이므로 3부터 시작
        assertTrue(optionalLink.isPresent());

        optionalLink = participationLinkRepository.findById(request.getModifyLinkInfo().getLinkIdsToRemove().getFirst());
        assertFalse(optionalLink.isPresent());

        assertEquals(2L, participationLinkTestRepository.countByParticipationId(participationId)); // 기존 2개에서 2개 추가하고 2개 제거
    }

    @Test
    @DisplayName("modifyLinkInfo가 null이어도 스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_ModifyLinkInfoNull_Success() {
        //when
        participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForModifyLinkInfoNullSuccess(),
                participationId,
                user1.getId()
        );

        //then
        assertEquals(2L, participationLinkTestRepository.countByParticipationId(participationId)); // 기존 2개에서 수정 없음
    }

    @Test
    @DisplayName("linksToAdd&linkIdsToRemove가 null이어도 스터디 참여 수정에 성공한다.")
    void updateStudyParticipation_LinksToAddNull_Success() {
        //when
        participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForLinksToAddNullSuccess(),
                participationId,
                user1.getId()
        );

        //then
        assertEquals(2L, participationLinkTestRepository.countByParticipationId(participationId)); // 기존 2개 그대로
    }

    @Test
    @DisplayName("링크 개수 제한을 넘으면 스터디 참여 수정에 실패한다.")
    void updateStudyParticipation_WhenExceedLinkLimit_Success() {
        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForExceedLinkLimitFail(),
                participationId,
                user1.getId()
        ));
    }

    @Test
    @DisplayName("tagIdsToRemove에 존재하지 않은 id값이 전달되면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenUnderTagLimit_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.createForWrongLinkIdToRemove(),
                participationId,
                user1.getId()
        ));
    }

    @Test
    @DisplayName("이미 승인된 스터디 참여이면 수정에 실패한다.")
    void updateStudyParticipation_WhenAlreadyAccepted_Success() {
        //given
        participationCommandService.updateStudyParticipationStatus(
                UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                participationId,
                user1.getId()
        );

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipation(
                UpdateStudyParticipationRequestFixture.create(),
                participationId,
                user1.getId()
        ));
    }

    @Test
    @DisplayName("스터디 참여 삭제에 성공한다.")
    void deleteStudyParticipation_Success() {
        //when
        participationCommandService.deleteStudyParticipation(participationId, user1.getId());

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(participationId);
        assertFalse(optionalParticipation.isPresent());

        assertEquals(0L, participationLinkRepository.count());
    }

    @Test
    @DisplayName("스터디 참여의 모집 포지션 수정에 성공한다.")
    void updateStudyParticipationPosition_Success() {
        //given
        Long anotherRecruitmentPositionId = 2L; // setUp에서 스터디 생성시 생성된 모집 포지션

        //when
        participationCommandService.updateStudyParticipationPosition(
                anotherRecruitmentPositionId,
                participationId,
                user1.getId()
        );

        //then
        StudyParticipation participation = participationRepository.findById(participationId).orElseThrow();
        assertEquals(anotherRecruitmentPositionId, participation.getRecruitmentPosition().getId());
    }

    @Test
    @DisplayName("동일하지 않은 스터디의 모집 포지션이면 스터디 참여 모집 포지션 수정에 실패한다.")
    void updateStudyParticipationPosition_WhenNewRecruitmentPositionIsAnotherStudy_ThenFail() {
        //given
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user1.getId());
        Long newRecruitmentPositionId = 5L; // 스터디를 생성할 때 4개씩 생성되므로 5부터 시작

        //when & then
        assertThrows(GlobalException.class, () -> participationCommandService.updateStudyParticipationPosition(
                newRecruitmentPositionId,
                participationId,
                user1.getId()
        ));
    }
}
