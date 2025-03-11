package com.jj.swm.domain.study.recruitmentposition.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.entity.StudyFixture;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.dto.request.UpdateRecruitmentPositionRequest;
import com.jj.swm.domain.study.recruitmentposition.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.recruitmentposition.fixture.request.RecruitmentPositionRequestFixture;
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

    //service
    @Autowired
    private RecruitmentPositionCommandService recruitmentPositionCommandService;

    //repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private RecruitmentPositionRepository recruitmentPositionRepository;

    // entity
    private User user;
    private Study study;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.createUser());
        study = studyRepository.save(StudyFixture.buildStudy(user));
        recruitmentPositionCommandService.addRecruitmentPosition(
                user.getId(),
                study.getId(),
                RecruitmentPositionRequestFixture.buildCreateRecruitmentPositionRequest()
        );
    }

    @Test
    @DisplayName("모집 포지션 생성에 성공한다.")
    void addRecruitmentPosition_Success() {
        //given
        CreateRecruitmentPositionRequest request =
                RecruitmentPositionRequestFixture.buildCreateRecruitmentPositionRequest();

        //when
        Long recruitmentPositionId = recruitmentPositionCommandService.addRecruitmentPosition(
                user.getId(),
                study.getId(),
                request
        ).getRecruitmentPositionId();

        //then
        Optional<StudyRecruitmentPosition> optionalRecruitmentPosition =
                recruitmentPositionRepository.findById(recruitmentPositionId);
        assertTrue(optionalRecruitmentPosition.isPresent());

        assertEquals(2L, recruitmentPositionId);
    }

    @Test
    @DisplayName("모집 포지션 최대 개수에 도달하면 생성에 실패한다.")
    void addRecruitmentPosition_FailByExceedLimit() {
        //given
        for (int i = 1; i <= 9; i++) {
            recruitmentPositionCommandService.addRecruitmentPosition(
                    user.getId(),
                    study.getId(),
                    RecruitmentPositionRequestFixture.buildCreateRecruitmentPositionRequest()
            );
        }

        //when & then
        assertThrows(GlobalException.class, () -> recruitmentPositionCommandService.addRecruitmentPosition(
                user.getId(),
                study.getId(),
                RecruitmentPositionRequestFixture.buildCreateRecruitmentPositionRequest()
        ));
    }

    @Test
    @DisplayName("모집 포지션 수정에 성공한다.")
    void modifyRecruitmentPosition_Success() {
        //given
        UpdateRecruitmentPositionRequest request =
                RecruitmentPositionRequestFixture.buildUpdateRecruitmentPositionRequest();

        //when
        recruitmentPositionCommandService.modifyRecruitmentPosition(
                user.getId(),
                1L,
                request
        );

        //then
        StudyRecruitmentPosition recruitmentPosition = recruitmentPositionRepository.findById(1L).get();

        assertEquals(request.getHeadcount(), recruitmentPosition.getHeadcount());
        assertEquals(request.getAcceptedCount(), recruitmentPosition.getAcceptedCount());
        assertEquals(request.getTitle(), recruitmentPosition.getTitle());
    }

    @Test
    @DisplayName("모집 인원보다 수락 인원이 많으면 모집 포지션 수정에 실패한다.")
    void modifyRecruitmentPosition_FailByAcceptedMoreThanHeadcount() {
        //given
        UpdateRecruitmentPositionRequest request =
                RecruitmentPositionRequestFixture.buildUpdateRecruitmentPositionRequestAcceptedMoreThanHeadcount();

        //when & then
        assertThrows(GlobalException.class,()->recruitmentPositionCommandService.modifyRecruitmentPosition(
                user.getId(),
                1L,
                request
        ));
    }

    @Test
    @DisplayName("모집 포지션 삭제에 성공한다.")
    void removeRecruitmentPosition_Success() {
        //when
        recruitmentPositionCommandService.removeRecruitmentPosition(user.getId(), 1L);

        //then
        assertEquals(0, recruitmentPositionRepository.count());
    }

    @Test
    @DisplayName("존재하지 않는 데이터여도 모집 포지션 삭제에 성공한다.")
    void removeRecruitmentPosition_WithoutExistsData_Success() {
        //when
        recruitmentPositionCommandService.removeRecruitmentPosition(user.getId(), 2L);

        //then
        assertEquals(1, recruitmentPositionRepository.count());
    }
}
