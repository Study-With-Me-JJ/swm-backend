package com.jj.swm.domain.study.recruitmentposition.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.entity.StudyFixture;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateRecruitmentPositionRequest;
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
    }

    @Test
    @DisplayName("모집 포지션 생성에 성공한다.")
    void addRecruitmentPosition_Success() {
        //given
        CreateRecruitmentPositionRequest createRecruitmentPositionRequest =
                RecruitmentPositionRequestFixture.buildCreateRecruitmentPositionRequest();

        //when
        Long recruitmentPositionId = recruitmentPositionCommandService.addRecruitmentPosition(
                user.getId(),
                study.getId(),
                createRecruitmentPositionRequest
        ).getRecruitmentPositionId();

        //then
        Optional<StudyRecruitmentPosition> optionalRecruitmentPosition =
                recruitmentPositionRepository.findById(recruitmentPositionId);
        assertTrue(optionalRecruitmentPosition.isPresent());

        assertEquals(1L, recruitmentPositionId);
    }

    @Test
    @DisplayName("모집 포지션 최대 개수에 도달하면 생성에 실패한다.")
    void addRecruitmentPosition_FailByExceedLimit() {
        //given
        for (int i = 1; i <= 10; i++) {
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
}
