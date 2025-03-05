package com.jj.swm.domain.study.core.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.core.dto.request.CreateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyStatusRequest;
import com.jj.swm.domain.study.core.dto.response.CreateStudyBookmarkResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyBookmark;
import com.jj.swm.domain.study.core.entity.StudyStatus;
import com.jj.swm.domain.study.core.fixture.request.StudyRequestFixture;
import com.jj.swm.domain.study.core.repository.*;
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
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jj.swm.domain.study.util.ConcurrencyTestUtils.THREAD_COUNT;
import static com.jj.swm.domain.study.util.ConcurrencyTestUtils.storeUserListAndBuildUserIdList;
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

    @Autowired
    private StudyBookmarkRepository studyBookmarkRepository;

    @Autowired
    private StudyLikeRepository studyLikeRepository;

    // entity
    private User user;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

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
    @DisplayName("태그 개수가 0보다 작으면 스터디 모집 수정에 실패한다.")
    void modifyStudy_FailByUnderTagLimit() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithUnderTagLimit();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(user.getId(), 1L, request));
    }

    @Test
    @DisplayName("태그 제한 개수를 초과하면 스터디 모집 수정에 실패한다.")
    void modifyStudy_FailByExceedTagLimit() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithExceedTagLimit();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(user.getId(), 1L, request));
    }

    @Test
    @DisplayName("이미지 개수가 0보다 작으면 스터디 모집 수정에 실패한다.")
    void modifyStudy_FailByUnderImageLimit() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithUnderImageLimit();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(user.getId(), 1L, request));
    }

    @Test
    @DisplayName("이미지 제한 개수를 초과하면 스터디 모집 수정에 실패한다.")
    void modifyStudy_FailByExceedImageLimit() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.buildUpdateStudyRequestWithExceedImageLimit();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(user.getId(), 1L, request));
    }

    @Test
    @DisplayName("스터디 상태 수정에 성공한다.")
    void modifyStudyStatus_Success() {
        //given
        UpdateStudyStatusRequest request = StudyRequestFixture.buildUpdateStudyStatusRequest();

        //when
        studyCommandService.modifyStudyStatus(user.getId(), 1L, request);

        //then
        Optional<Study> optionalStudy = studyRepository.findById(1L);
        assertTrue(optionalStudy.isPresent());
        Study study = optionalStudy.get();
        assertEquals(StudyStatus.INACTIVE, study.getStatus());
    }

    @Test
    @DisplayName("스터디 모집 북마크 생성에 성공한다.")
    void addStudyBookmark_Success() {
        //when
        studyCommandService.addStudyBookmark(user.getId(), 1L);

        //then
        Optional<StudyBookmark> optionalStudyBookmark = studyBookmarkRepository.findById(1L);
        assertTrue(optionalStudyBookmark.isPresent());
    }

    @Test
    @DisplayName("이미 북마크한 것에 북마크하면 기존 북마크 정보를 반환하는 것에 성공한다.")
    void addStudyBookmark_AlreadyExists_Success() {
        //given
        studyCommandService.addStudyBookmark(user.getId(), 1L);

        //when
        CreateStudyBookmarkResponse response = studyCommandService.addStudyBookmark(user.getId(), 1L);

        //then
        assertEquals(1L, response.getBookmarkId());

        long count = studyBookmarkRepository.count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("스터디 모집 북마크 삭제에 성공한다.")
    void removeStudyBookmark_Success() {
        //given
        studyCommandService.addStudyBookmark(user.getId(), 1L);

        //when
        studyCommandService.removeStudyBookmark(user.getId(), 1L);

        //then
        Optional<StudyBookmark> optionalStudyBookmark = studyBookmarkRepository.findById(1L);
        assertFalse(optionalStudyBookmark.isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 스터디 모집 북마크에 대해 삭제해도 성공한다.")
    void removeStudyBookmark_NonExists_Success() {
        //when & then
        assertDoesNotThrow(() -> studyCommandService.addStudyBookmark(user.getId(), 1L));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 생성에 성공한다.")
    void addStudyLike_Success() {
        //when
        studyCommandService.addStudyLike(user.getId(), 1L);

        //then
        boolean result = studyLikeRepository.existsByUserIdAndStudyId(user.getId(), 1L);
        assertTrue(result);

        Study study = studyRepository.findById(1L).get();
        assertEquals(1, study.getLikeCount());
    }

    @Test
    @DisplayName("이미 좋아요한 것에 좋아요해도 성공한다.")
    void addStudyLike_AlreadyExists_Success() {
        //given
        studyCommandService.addStudyLike(user.getId(), 1L);

        //when & then
        assertDoesNotThrow(() -> studyCommandService.addStudyLike(user.getId(), 1L));

        assertEquals(1, studyLikeRepository.count());
    }

    @Test
    @DisplayName("스터디 모집 좋아요 동시성 제어에 성공한다.")
    void addStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        List<UUID> userIdList = storeUserListAndBuildUserIdList(userRepository);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = userIdList.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.addStudyLike(userId, 1L);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        //then
        assertEquals(THREAD_COUNT, studyLikeRepository.count());

        Study study = studyRepository.findById(1L).get();
        assertEquals(THREAD_COUNT, study.getLikeCount());
    }

    @Test
    @DisplayName("스터디 모집 좋아요 삭제에 성공한다.")
    void removeStudyLike_Success() {
        //given
        studyCommandService.addStudyLike(user.getId(), 1L);

        //when
        studyCommandService.removeStudyLike(user.getId(), 1L);

        //then
        boolean result = studyLikeRepository.existsByUserIdAndStudyId(user.getId(), 1L);
        assertFalse(result);

        Study study = studyRepository.findById(1L).get();
        assertEquals(0, study.getLikeCount());
    }

    @Test
    @DisplayName("존재하지 않는 스터디 모집 좋아요에 대해 삭제해도 성공한다.")
    void removeStudyLike_NonExists_Success() {
        //when & then
        assertDoesNotThrow(() -> studyCommandService.removeStudyLike(user.getId(), 1L));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 취소 동시성 제어에 성공한다.")
    void removeStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        List<UUID> userIdList = storeUserListAndBuildUserIdList(userRepository);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        for (UUID userId : userIdList) {
            studyCommandService.addStudyLike(userId, 1L);
        }

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = userIdList.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.removeStudyLike(userId, 1L);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        //then
        assertEquals(0, studyLikeRepository.count());

        Study study = studyRepository.findById(1L).get();
        assertEquals(0, study.getLikeCount());
    }
}
