package com.jj.swm.domain.study.core.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.dto.request.CreateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.fixture.request.StudyRequestFixture;
import com.jj.swm.domain.study.core.repository.StudyImageRepository;
import com.jj.swm.domain.study.core.repository.StudyRepository;
import com.jj.swm.domain.study.core.repository.StudyTagRepository;
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

class StudyCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    // service
    @Autowired
    private StudyCommandService studyCommandService;

    // repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyImageRepository studyImageRepository;

    @Autowired
    private StudyTagRepository studyTagRepository;

    // entity
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.createUser());

        studyCommandService.addStudy(user.getId(), StudyRequestFixture.buildCreateStudyRequest());
    }

    @Test
    @DisplayName("스터디 모집 생성에 성공한다.")
    void addStudy_Success() {
        //given
        CreateStudyRequest request = StudyRequestFixture.buildCreateStudyRequest();

        //when
        studyCommandService.addStudy(user.getId(), request);

        //then
        Optional<Study> optionalStudy = studyRepository.findById(2L);

        assertTrue(optionalStudy.isPresent());
    }

    @Test
    @DisplayName("tag&imageUrlList가 없어도 스터디 모집 생성에 성공한다.")
    void addStudy_WithoutTagAndImageList_Success() {
        //given
        CreateStudyRequest request = StudyRequestFixture.buildCreateStudyRequestWithoutTagAndImageList();

        //when
        studyCommandService.addStudy(user.getId(), request);

        //then
        Optional<Study> optionalStudy = studyRepository.findById(2L);

        assertTrue(optionalStudy.isPresent());
    }

    @Test
    @DisplayName("스터디 모집 수정에 성공한다")
    void modifyStudy_Success() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequest();

        //when
        studyCommandService.modifyStudy(user.getId(), 1L, request);

        //then
        Optional<Study> optionalStudy = studyRepository.findById(1L);
        assertTrue(optionalStudy.isPresent());
        Study study = optionalStudy.get();
        assertEquals(study.getTitle(), request.getTitle());
        assertEquals(study.getContent(), request.getContent());
        assertEquals(study.getOpenChatUrl(), request.getOpenChatUrl());
        assertEquals(study.getCategory(), request.getCategory());
        assertEquals(3, studyTagRepository.countByStudyId(1L)); // 기존 데이터 2개에서 1개 제거하고 2개 추가
        assertEquals(3, studyImageRepository.countByStudyId(1L)); // 기존 데이터 2개에서 1개 제거하고 2개 추가
    }

    @Test
    @DisplayName("SaveTag&ImageRequest가 없어도 스터디 모집 수정에 성공한다")
    void modifyStudy_WithoutSaveTagAndImageRequest_Success() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithoutSaveTagAndImageRequest();

        //when
        studyCommandService.modifyStudy(user.getId(), 1L, request);

        //then
        Optional<Study> optionalStudy = studyRepository.findById(1L);
        assertTrue(optionalStudy.isPresent());
        Study study = optionalStudy.get();
        assertEquals(study.getTitle(), request.getTitle());
        assertEquals(study.getContent(), request.getContent());
        assertEquals(study.getOpenChatUrl(), request.getOpenChatUrl());
        assertEquals(study.getCategory(), request.getCategory());
        assertEquals(2, studyTagRepository.countByStudyId(1L)); // 기존 데이터 2개
        assertEquals(2, studyImageRepository.countByStudyId(1L)); // 기존 데이터 2개
    }

    @Test
    @DisplayName("Tag&ImageListToAdd가 없어도 스터디 모집 수정에 성공한다")
    void modifyStudy_WithoutTagAndImageListToAdd_Success() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithoutTagAndImageListToAdd();

        //when
        studyCommandService.modifyStudy(user.getId(), 1L, request);

        //then
        Optional<Study> optionalStudy = studyRepository.findById(1L);
        assertTrue(optionalStudy.isPresent());
        Study study = optionalStudy.get();
        assertEquals(study.getTitle(), request.getTitle());
        assertEquals(study.getContent(), request.getContent());
        assertEquals(study.getOpenChatUrl(), request.getOpenChatUrl());
        assertEquals(study.getCategory(), request.getCategory());
        assertEquals(1, studyTagRepository.countByStudyId(1L)); // 기존 데이터 2개에서 1개 제거
        assertEquals(1, studyImageRepository.countByStudyId(1L)); // 기존 데이터 2개에서 1개 제거
    }

    @Test
    @DisplayName("Tag&ImageIdListToRemove가 없어도 스터디 모집 수정에 성공한다")
    void modifyStudy_WithoutTagAndImageIdListToRemove_Success() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithoutTagAndImageIdListToRemove();

        //when
        studyCommandService.modifyStudy(user.getId(), 1L, request);

        //then
        Optional<Study> optionalStudy = studyRepository.findById(1L);
        assertTrue(optionalStudy.isPresent());
        Study study = optionalStudy.get();
        assertEquals(study.getTitle(), request.getTitle());
        assertEquals(study.getContent(), request.getContent());
        assertEquals(study.getOpenChatUrl(), request.getOpenChatUrl());
        assertEquals(study.getCategory(), request.getCategory());
        assertEquals(4, studyTagRepository.countByStudyId(1L)); // 기존 데이터 2개에서 2개 추가
        assertEquals(4, studyImageRepository.countByStudyId(1L)); // 기존 데이터 2개에서 2개 추가
    }

    @Test
    @DisplayName("태그 개수가 0보다 작으면 스터디 모집 수정에 실패한다")
    void modifyStudy_FailedByUnderTagLimit() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithUnderTagLimit();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(user.getId(), 1L, request));
    }

    @Test
    @DisplayName("태그 제한 개수를 초과하면 스터디 모집 수정에 실패한다")
    void modifyStudy_FailedByExceedTagLimit() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithExceedTagLimit();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(user.getId(), 1L, request));
    }

    @Test
    @DisplayName("이미지 개수가 0보다 작으면 스터디 모집 수정에 실패한다")
    void modifyStudy_FailedByUnderImageLimit() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithUnderImageLimit();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(user.getId(), 1L, request));
    }

    @Test
    @DisplayName("이미지 제한 개수를 초과하면 스터디 모집 수정에 실패한다")
    void modifyStudy_FailedByExceedImageLimit() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithExceedImageLimit();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(user.getId(), 1L, request));
    }
}
