package com.jj.swm.domain.study.recruitmentposition.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpsertRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyParticipation;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.recruitmentposition.fixture.dto.request.CreateStudyParticipationRequestFixture;
import com.jj.swm.domain.study.recruitmentposition.fixture.dto.request.UpsertRecruitmentPositionRequestFixture;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.study.recruitmentposition.repository.StudyParticipationAttachmentRepository;
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

    @Autowired
    private StudyParticipationAttachmentRepository participationAttachmentRepository;

    // entity
    private User user;
    private final Long studyId = 1L;
    private final Long recruitmentPositionId = 1L; // addStudy 할 시에 모집 포지션 2개 삽입

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
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

    //TODO 승인 로직 하면 수정 실패 테스트 하기

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
        Long newParticipationId = 1L;

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(newParticipationId);
        assertTrue(optionalParticipation.isPresent());

        StudyParticipation participation = optionalParticipation.get();
        assertEquals(request.getCoverLetter(), participation.getCoverLetter());

        assertEquals(request.getLinks().size(), participationLinkRepository.count());
        assertEquals(request.getFileUrls().size(), participationAttachmentRepository.count());
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
        Long newParticipationId = 1L;

        //then
        Optional<StudyParticipation> optionalParticipation = participationRepository.findById(newParticipationId);
        assertTrue(optionalParticipation.isPresent());
    }

    //TODO 승인 api 구현되면 테스트 하기
//    @Test
//    @DisplayName("이미 모집 인원 수만큼 승인 수가 채워졌으면 참여 생성에 실패한다.")
//    void createStudyParticipation_WhenAcceptedCountEqualHeadcount_ThenFail() {
//
//    }
}
