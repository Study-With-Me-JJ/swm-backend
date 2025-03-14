package com.jj.swm.domain.study.core.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.request.UpsertCommentRequest;
import com.jj.swm.domain.study.comment.fixture.CommentRequestFixture;
import com.jj.swm.domain.study.comment.repository.CommentRepository;
import com.jj.swm.domain.study.comment.service.CommentCommandService;
import com.jj.swm.domain.study.core.dto.request.CreateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyStatusRequest;
import com.jj.swm.domain.study.core.dto.response.CreateStudyBookmarkResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyBookmark;
import com.jj.swm.domain.study.core.fixture.StudyRequestFixture;
import com.jj.swm.domain.study.core.repository.*;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
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

import static com.jj.swm.domain.user.helper.UserTestHelper.insertUsersAndGetUserIds;
import static org.junit.jupiter.api.Assertions.*;

class StudyCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    private static final int THREAD_COUNT = 100;

    // target service
    @Autowired
    private StudyCommandService studyCommandService;

    // service
    @Autowired
    private CommentCommandService commentCommandService;

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

    @Autowired
    private RecruitmentPositionRepository recruitmentPositionRepository;

    @Autowired
    private CommentRepository commentRepository;

    // entity
    private User user;

    private final Long studyId = 1L; // setUp 시 생성된 스터디 모집의 id값

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.createUser());

        studyCommandService.addStudy(user.getId(), StudyRequestFixture.createStudyRequest());
    }

    @Test
    @DisplayName("스터디 모집 생성에 성공한다.")
    void addStudy_Success() {
        //given
        CreateStudyRequest request = StudyRequestFixture.createStudyRequest();

        //when
        studyCommandService.addStudy(user.getId(), request);
        Long newStudyId = 2L; // setUp에서 1L 생성되고 이 메소드에서 하나 더 생성하므로 id 값은 2L

        //then
        Optional<Study> optionalStudy = studyRepository.findById(newStudyId);
        assertTrue(optionalStudy.isPresent());

        Study study = optionalStudy.get();
        assertEquals(request.getTitle(), study.getTitle());

        assertEquals(request.getTagList().size(), studyTagRepository.countByStudyId(newStudyId));
        assertEquals(request.getImageUrlList().size(), studyImageRepository.countByStudyId(newStudyId));
        assertEquals(
                request.getCreateRecruitmentPositionRequestList().size(),
                recruitmentPositionRepository.countByStudyId(newStudyId)
        );
    }

    @Test
    @DisplayName("tag&imageUrlList가 없어도 스터디 모집 생성에 성공한다.")
    void addStudy_WithoutTagAndImageList_Success() {
        //when
        studyCommandService.addStudy(user.getId(), StudyRequestFixture.createStudyRequestWithoutTagAndImageList());
        Long newStudyId = 2L;

        //then
        Optional<Study> optionalStudy = studyRepository.findById(newStudyId);

        assertTrue(optionalStudy.isPresent());
    }

    @Test
    @DisplayName("스터디 모집 수정에 성공한다")
    void modifyStudy_Success() {
        //given
        UpdateStudyRequest request = StudyRequestFixture.updateStudyRequest();

        //when
        studyCommandService.modifyStudy(
                user.getId(),
                studyId,
                request
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(request.getTitle(), study.getTitle());

        assertEquals(3, studyTagRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 1개 제거하고 2개 추가
        assertEquals(3, studyImageRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 1개 제거하고 2개 추가
    }

    @Test
    @DisplayName("SaveTag&ImageRequest가 없어도 스터디 모집 수정에 성공한다.")
    void modifyStudy_WithoutSaveTagAndImageRequest_Success() {
        //when
        studyCommandService.modifyStudy(
                user.getId(),
                studyId,
                StudyRequestFixture.updateStudyRequestWithoutSaveTagAndImageRequest()
        );

        //then
        assertEquals(2, studyTagRepository.countByStudyId(studyId)); // 기존 데이터 2개
        assertEquals(2, studyImageRepository.countByStudyId(studyId)); // 기존 데이터 2개
    }

    @Test
    @DisplayName("Tag&ImageListToAdd가 없어도 스터디 모집 수정에 성공한다.")
    void modifyStudy_WithoutTagAndImageListToAdd_Success() {
        //when
        studyCommandService.modifyStudy(
                user.getId(),
                studyId,
                StudyRequestFixture.updateStudyRequestWithoutTagAndImageListToAdd()
        );

        //then
        assertEquals(1, studyTagRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 1개 제거
        assertEquals(1, studyImageRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 1개 제거
    }

    @Test
    @DisplayName("Tag&ImageIdListToRemove가 없어도 스터디 모집 수정에 성공한다.")
    void modifyStudy_WithoutTagAndImageIdListToRemove_Success() {
        //when
        studyCommandService.modifyStudy(
                user.getId(),
                studyId,
                StudyRequestFixture.updateStudyRequestWithoutTagAndImageIdListToRemove()
        );

        //then
        assertEquals(4, studyTagRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 2개 추가
        assertEquals(4, studyImageRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 2개 추가
    }

    @Test
    @DisplayName("태그 개수가 0보다 작으면 스터디 모집 수정에 실패한다.")
    void modifyStudy_FailByUnderTagLimit() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(
                user.getId(),
                studyId,
                StudyRequestFixture.updateStudyRequestWithUnderTagLimit()
        ));
    }

    @Test
    @DisplayName("태그 제한 개수를 초과하면 스터디 모집 수정에 실패한다.")
    void modifyStudy_FailByExceedTagLimit() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(
                user.getId(),
                studyId,
                StudyRequestFixture.updateStudyRequestWithExceedTagLimit()
        ));
    }

    @Test
    @DisplayName("이미지 개수가 0보다 작으면 스터디 모집 수정에 실패한다.")
    void modifyStudy_FailByUnderImageLimit() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(
                user.getId(),
                studyId,
                StudyRequestFixture.updateStudyRequestWithUnderImageLimit()
        ));
    }

    @Test
    @DisplayName("이미지 제한 개수를 초과하면 스터디 모집 수정에 실패한다.")
    void modifyStudy_FailByExceedImageLimit() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyStudy(
                user.getId(),
                studyId,
                StudyRequestFixture.updateStudyRequestWithExceedImageLimit()
        ));
    }

    @Test
    @DisplayName("스터디 모집 상태 수정에 성공한다.")
    void modifyStudyStatus_Success() {
        //given
        UpdateStudyStatusRequest request = StudyRequestFixture.updateStudyStatusRequest();

        //when
        studyCommandService.modifyStudyStatus(
                user.getId(),
                studyId,
                request
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(request.getStatus(), study.getStatus());
    }

    @Test
    @DisplayName("스터디 모집 북마크 생성에 성공한다.")
    void addStudyBookmark_Success() {
        //when
        Long bookmarkId = studyCommandService.addStudyBookmark(user.getId(), studyId).getBookmarkId();

        //then
        Optional<StudyBookmark> optionalStudyBookmark = studyBookmarkRepository.findById(bookmarkId);
        assertTrue(optionalStudyBookmark.isPresent());
    }

    @Test
    @DisplayName("이미 북마크한 것에 북마크하면 기존 북마크 정보를 반환하는 것에 성공한다.")
    void addStudyBookmark_AlreadyExists_Success() {
        //given
        Long bookmarkId = studyCommandService.addStudyBookmark(user.getId(), studyId).getBookmarkId();

        //when
        CreateStudyBookmarkResponse response = studyCommandService.addStudyBookmark(user.getId(), studyId);

        //then
        assertEquals(bookmarkId, response.getBookmarkId());

        long count = studyBookmarkRepository.count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("스터디 모집 북마크 삭제에 성공한다.")
    void removeStudyBookmark_Success() {
        //given
        Long bookmarkId = studyCommandService.addStudyBookmark(user.getId(), studyId).getBookmarkId();

        //when
        studyCommandService.removeStudyBookmark(user.getId(), bookmarkId);

        //then
        Optional<StudyBookmark> optionalStudyBookmark = studyBookmarkRepository.findById(bookmarkId);
        assertFalse(optionalStudyBookmark.isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 스터디 모집 북마크에 대해 삭제해도 성공한다.")
    void removeStudyBookmark_NonExists_Success() {
        //when & then
        assertDoesNotThrow(() -> studyCommandService.removeStudyBookmark(user.getId(), 123456789L));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 생성에 성공한다.")
    void addStudyLike_Success() {
        //when
        studyCommandService.addStudyLike(user.getId(), studyId);

        //then
        boolean result = studyLikeRepository.existsByUserIdAndStudyId(user.getId(), studyId);
        assertTrue(result);

        Study study = studyRepository.findById(studyId).get();
        assertEquals(1, study.getLikeCount());
    }

    @Test
    @DisplayName("이미 좋아요한 것에 좋아요해도 성공한다.")
    void addStudyLike_AlreadyExists_Success() {
        //given
        studyCommandService.addStudyLike(user.getId(), studyId);

        //when & then
        assertDoesNotThrow(() -> studyCommandService.addStudyLike(user.getId(), studyId));

        assertEquals(1, studyLikeRepository.count());
    }

    @Test
    @DisplayName("스터디 모집 좋아요 동시성 제어에 성공한다.")
    void addStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        List<UUID> userIdList = insertUsersAndGetUserIds(userRepository, THREAD_COUNT);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = userIdList.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.addStudyLike(userId, studyId);
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

        Study study = studyRepository.findById(studyId).get();
        assertEquals(THREAD_COUNT, study.getLikeCount());
    }

    @Test
    @DisplayName("스터디 모집 좋아요 삭제에 성공한다.")
    void removeStudyLike_Success() {
        //given
        studyCommandService.addStudyLike(user.getId(), studyId);

        //when
        studyCommandService.removeStudyLike(user.getId(), studyId);

        //then
        boolean result = studyLikeRepository.existsByUserIdAndStudyId(user.getId(), studyId);
        assertFalse(result);

        Study study = studyRepository.findById(1L).get();
        assertEquals(0, study.getLikeCount());
    }

    @Test
    @DisplayName("존재하지 않는 스터디 모집 좋아요에 대해 삭제해도 성공한다.")
    void removeStudyLike_NonExists_Success() {
        //when & then
        assertDoesNotThrow(() -> studyCommandService.removeStudyLike(user.getId(), studyId));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 취소 동시성 제어에 성공한다.")
    void removeStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        List<UUID> userIdList = insertUsersAndGetUserIds(userRepository, THREAD_COUNT);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        for (UUID userId : userIdList) {
            studyCommandService.addStudyLike(userId, studyId);
        }

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = userIdList.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.removeStudyLike(userId, studyId);
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

    @Test
    @DisplayName("스터디 모집 삭제에 성공한다.")
    void removeStudy_Success() {
        //given
        studyCommandService.addStudyLike(user.getId(), studyId);
        studyCommandService.addStudyBookmark(user.getId(), studyId);

        UpsertCommentRequest createRequest = CommentRequestFixture.createCommentRequest();
        Long parentId = commentCommandService.addComment(
                user.getId(),
                studyId,
                null,
                createRequest
        ).getCommentId();
        commentCommandService.addComment(
                user.getId(),
                studyId,
                parentId,
                createRequest
        );

        //when
        studyCommandService.removeStudy(user.getId(), 1L);

        //then
        assertEquals(0, studyTagRepository.count());
        assertEquals(0, studyImageRepository.count());
        assertEquals(0, recruitmentPositionRepository.count());
        assertEquals(0, studyLikeRepository.count());
        assertEquals(0, commentRepository.count());
        assertEquals(0, studyBookmarkRepository.count());
        assertEquals(0, studyRepository.count());
    }

    @Test
    @DisplayName("스터디 모집 다중 삭제에 성공한다.")
    void removeStudyList_Success() {
        //given
        studyCommandService.addStudy(user.getId(), StudyRequestFixture.createStudyRequest());
        Long newStudyId = 2L;

        studyCommandService.addStudyLike(user.getId(), studyId);
        studyCommandService.addStudyBookmark(user.getId(), studyId);

        studyCommandService.addStudyLike(user.getId(), newStudyId);
        studyCommandService.addStudyBookmark(user.getId(), newStudyId);

        UpsertCommentRequest createRequest = CommentRequestFixture.createCommentRequest();
        Long parentId1 = commentCommandService.addComment(
                user.getId(),
                studyId,
                null,
                createRequest
        ).getCommentId();
        commentCommandService.addComment(
                user.getId(),
                newStudyId,
                parentId1,
                createRequest
        );

        Long parentId2 = commentCommandService.addComment(
                user.getId(),
                newStudyId,
                null,
                createRequest
        ).getCommentId();
        commentCommandService.addComment(
                user.getId(),
                newStudyId,
                parentId2,
                createRequest
        );

        //when
        studyCommandService.removeStudyList(
                user.getId(),
                StudyRequestFixture.deleteStudyListRequest(List.of(studyId, newStudyId))
        );

        //then
        assertEquals(0, studyTagRepository.count());
        assertEquals(0, studyImageRepository.count());
        assertEquals(0, recruitmentPositionRepository.count());
        assertEquals(0, studyLikeRepository.count());
        assertEquals(0, commentRepository.count());
        assertEquals(0, studyBookmarkRepository.count());
        assertEquals(0, studyRepository.count());
    }

    @Test
    @DisplayName("삭제할 스터디가 존재하지 않으면 스터디 모집 다중 삭제에 실패한다.")
    void removeStudyList_FailByNotExist() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.removeStudyList(
                user.getId(),
                StudyRequestFixture.deleteStudyListRequest(List.of(studyId, 123456789L)))
        );
    }
}
