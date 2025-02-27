package com.jj.swm.domain.study.core.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.dto.request.CreateStudyRequest;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.request.StudyRequestFixture;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StudyCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    @Autowired
    private StudyCommandService studyCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyRepository studyRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.createUser());
    }

    @Test
    @DisplayName("스터디 모집 생성에 성공한다.")
    void addStudy_Success() {
        //given
        CreateStudyRequest request = StudyRequestFixture.buildCreateStudyRequest();

        //when
        studyCommandService.addStudy(user.getId(), request);

        //then
        Optional<Study> optionalStudy = studyRepository.findById(1L);

        assertTrue(optionalStudy.isPresent());
    }

    @Test
    @DisplayName("태그와 이미지 리스트가 없어도 스터디 모집 생성에 성공한다.")
    void addStudy_WithoutTagAndImageList_Success() {
        //given
        CreateStudyRequest request = StudyRequestFixture.buildCreateStudyRequestWithoutTagAndImageList();

        //when
        studyCommandService.addStudy(user.getId(), request);

        //then
        Optional<Study> optionalStudy = studyRepository.findById(1L);

        assertTrue(optionalStudy.isPresent());
    }
}
