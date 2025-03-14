package com.jj.swm.domain.study.recruitmentposition.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.fixture.StudyRequestFixture;
import com.jj.swm.domain.study.core.service.StudyCommandService;
import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.recruitmentposition.fixture.RecruitmentPositionRequestFixture;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
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

    // entity
    private User user;
    private final Long studyId = 1L;
    private final Long recruitmentPositionId = 1L; // addStudy 할 시에 모집 포지션 2개 삽입

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.createUser());
        studyCommandService.createStudy(StudyRequestFixture.createStudyRequest(), user.getId());
    }

    @Test
    @DisplayName("모집 포지션 생성에 성공한다.")
    void createRecruitmentPosition_Success() {
        //given
        CreateRecruitmentPositionRequest request =
                RecruitmentPositionRequestFixture.createRecruitmentPositionRequest();

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
    void createRecruitmentPosition_WhenExceedLimit_thenFail() {
        //given
        CreateRecruitmentPositionRequest request =
                RecruitmentPositionRequestFixture.createRecruitmentPositionRequest();

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
        UpdateRecruitmentPositionRequest request =
                RecruitmentPositionRequestFixture.updateRecruitmentPositionRequest();

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
        assertEquals(request.getAcceptedCount(), recruitmentPosition.getAcceptedCount());
        assertEquals(request.getTitle(), recruitmentPosition.getTitle());
    }

    @Test
    @DisplayName("모집 인원보다 수락 인원이 많으면 모집 포지션 수정에 실패한다.")
    void updateRecruitmentPosition_WhenAcceptedMoreThanHeadcount_thenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.updateRecruitmentPosition(
                RecruitmentPositionRequestFixture.updateRecruitmentPositionRequestAcceptedMoreThanHeadcount(),
                recruitmentPositionId,
                user.getId()
        ));
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
    @DisplayName("존재하지 않는 데이터여도 모집 포지션 삭제에 성공한다.")
    void deleteRecruitmentPosition_WithoutExistsData_Success() {
        //when
        recruitmentPositionCommandService.deleteRecruitmentPosition(123456789L, user.getId());

        //then
        assertEquals(2, recruitmentPositionRepository.count());
    }
}
