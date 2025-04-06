package com.jj.swm.domain.study.core.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.request.UpsertStudyCommentRequest;
import com.jj.swm.domain.study.comment.fixture.dto.request.UpsertStudyCommentRequestFixture;
import com.jj.swm.domain.study.comment.repository.StudyStudyCommentRepository;
import com.jj.swm.domain.study.comment.service.StudyCommentCommandService;
import com.jj.swm.domain.study.core.dto.request.CreateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyStatusRequest;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyBookmark;
import com.jj.swm.domain.study.core.fixture.dto.request.CreateStudyRequestFixture;
import com.jj.swm.domain.study.core.fixture.dto.request.DeleteStudiesRequestFixture;
import com.jj.swm.domain.study.core.fixture.dto.request.UpdateStudyRequestFixture;
import com.jj.swm.domain.study.core.fixture.dto.request.UpdateStudyStatusRequestFixture;
import com.jj.swm.domain.study.core.repository.*;
import com.jj.swm.domain.study.participation.fixture.dto.request.CreateStudyParticipationRequestFixture;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.domain.study.participation.service.StudyParticipationCommandService;
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
    private StudyCommentCommandService commentCommandService;

    @Autowired
    private StudyParticipationCommandService participationCommandService;

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
    private StudyStudyCommentRepository commentRepository;

    @Autowired
    private StudyParticipationRepository participationRepository;

    @Autowired
    private StudyParticipationLinkRepository participationLinkRepository;

    // entity
    private User user;
    private final Long studyId = 1L; // setUp 시 생성된 스터디 모집의 id값
    private final Long recruitmentPositionId = 1L; // setUp 시 생성된 4개의 모집 포지션 중 하나의 id 값

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());

        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
    }

    @Test
    @DisplayName("스터디 모집 생성에 성공한다.")
    void createStudy_Success() {
        //given
        CreateStudyRequest request = CreateStudyRequestFixture.create();

        //when
        studyCommandService.createStudy(request, user.getId());
        Long newStudyId = 2L; // setUp에서 1L 생성되고 이 메소드에서 하나 더 생성하므로 id 값은 2L

        //then
        Optional<Study> optionalStudy = studyRepository.findById(newStudyId);
        assertTrue(optionalStudy.isPresent());

        Study study = optionalStudy.get();
        assertEquals(request.getTitle(), study.getTitle());

        assertEquals(request.getTags().size(), studyTagRepository.countByStudyId(newStudyId));
        assertEquals(request.getImageUrls().size(), studyImageRepository.countByStudyId(newStudyId));
        assertEquals(
                request.getCreateRecruitmentPositionRequests().size(),
                recruitmentPositionRepository.countByStudyId(newStudyId)
        );
    }

    @Test
    @DisplayName("tags&imageUrls가 없어도 스터디 모집 생성에 성공한다.")
    void createStudy_WithoutTagAndImages_Success() {
        //when
        studyCommandService.createStudy(CreateStudyRequestFixture.createForNoTagImagesSuccess(), user.getId());
        Long newStudyId = 2L;

        //then
        Optional<Study> optionalStudy = studyRepository.findById(newStudyId);

        assertTrue(optionalStudy.isPresent());
    }

    @Test
    @DisplayName("스터디 모집 수정에 성공한다")
    void updateStudy_Success() {
        //given
        UpdateStudyRequest request = UpdateStudyRequestFixture.create();

        //when
        studyCommandService.updateStudy(
                request,
                studyId,
                user.getId()
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(request.getTitle(), study.getTitle());

        assertEquals(3, studyTagRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 1개 제거하고 2개 추가
        assertEquals(3, studyImageRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 1개 제거하고 2개 추가
    }

    @Test
    @DisplayName("SaveTag&ImageRequest가 없어도 스터디 모집 수정에 성공한다.")
    void updateStudy_WithoutSaveTagAndImageRequest_Success() {
        //when
        studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForNoModifyTagAndImageRequestSuccess(),
                studyId,
                user.getId()
        );

        //then
        assertEquals(2, studyTagRepository.countByStudyId(studyId)); // 기존 데이터 2개
        assertEquals(2, studyImageRepository.countByStudyId(studyId)); // 기존 데이터 2개
    }

    @Test
    @DisplayName("Tags&ImagesToAdd가 없어도 스터디 모집 수정에 성공한다.")
    void updateStudy_WithoutTagAndImagesToAdd_Success() {
        //when
        studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForNoTagAndImagesToAddSuccess(),
                studyId,
                user.getId()
        );

        //then
        assertEquals(1, studyTagRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 1개 제거
        assertEquals(1, studyImageRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 1개 제거
    }

    @Test
    @DisplayName("Tag&ImageIdsToRemove가 없어도 스터디 모집 수정에 성공한다.")
    void updateStudy_WithoutTagAndImageIdsToRemove_Success() {
        //when
        studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createFroNoTagAndImageIdsToRemoveSuccess(),
                studyId,
                user.getId()
        );

        //then
        assertEquals(4, studyTagRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 2개 추가
        assertEquals(4, studyImageRepository.countByStudyId(studyId)); // 기존 데이터 2개에서 2개 추가
    }

    @Test
    @DisplayName("태그 개수가 0보다 작으면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenUnderTagLimit_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForUnderTagLimitFail(),
                studyId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("태그 제한 개수를 초과하면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenExceedTagLimit_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForExceedTagLimitFail(),
                studyId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("이미지 개수가 0보다 작으면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenUnderImageLimit_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForUnderImageLimitFail(),
                studyId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("이미지 제한 개수를 초과하면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenExceedImageLimit_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForExceedImageLimitFail(),
                studyId,
                user.getId()
        ));
    }

    @Test
    @DisplayName("스터디 모집 상태 수정에 성공한다.")
    void updateStudyStatus_Success() {
        //given
        UpdateStudyStatusRequest request = UpdateStudyStatusRequestFixture.create();

        //when
        studyCommandService.updateStudyStatus(
                request,
                studyId,
                user.getId()
        );

        //then
        Study study = studyRepository.findById(studyId).get();
        assertEquals(request.getStatus(), study.getStatus());
    }

    @Test
    @DisplayName("스터디 모집 북마크 생성에 성공한다.")
    void createStudyBookmark_Success() {
        //when
        Long bookmarkId = studyCommandService.createStudyBookmark(studyId, user.getId()).getBookmarkId();

        //then
        Optional<StudyBookmark> optionalStudyBookmark = studyBookmarkRepository.findById(bookmarkId);
        assertTrue(optionalStudyBookmark.isPresent());
    }

    @Test
    @DisplayName("이미 북마크한 것에 북마크하면 실패한다.")
    void createStudyBookmark_WhenAlreadyExists_ThenFail() {
        //given
        studyCommandService.createStudyBookmark(studyId, user.getId());

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.createStudyBookmark(studyId, user.getId()));
    }

    @Test
    @DisplayName("스터디 모집 북마크 삭제에 성공한다.")
    void deleteStudyBookmark_Success() {
        //given
        Long bookmarkId = studyCommandService.createStudyBookmark(studyId, user.getId()).getBookmarkId();

        //when
        studyCommandService.deleteStudyBookmark(bookmarkId, user.getId());

        //then
        Optional<StudyBookmark> optionalStudyBookmark = studyBookmarkRepository.findById(bookmarkId);
        assertFalse(optionalStudyBookmark.isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 스터디 모집 북마크에 대해 삭제하면 실패한다.")
    void deleteStudyBookmark_WhenNonExists_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.deleteStudyBookmark(123456789L, user.getId()));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 생성에 성공한다.")
    void createStudyLike_Success() {
        //when
        studyCommandService.createStudyLike(studyId, user.getId());

        //then
        boolean result = studyLikeRepository.existsByStudyIdAndUserId(studyId, user.getId());
        assertTrue(result);

        Study study = studyRepository.findById(studyId).get();
        assertEquals(1, study.getLikeCount());
    }

    @Test
    @DisplayName("이미 좋아요한 것에 좋아요하면 실패한다.")
    void createStudyLike_WhenAlreadyExists_ThenFail() {
        //given
        studyCommandService.createStudyLike(studyId, user.getId());

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.createStudyLike(studyId, user.getId()));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 동시성 제어에 성공한다.")
    void createStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        List<UUID> userIds = insertUsersAndGetUserIds(userRepository, THREAD_COUNT);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = userIds.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.createStudyLike(studyId, userId);
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
    void deleteStudyLike_Success() {
        //given
        studyCommandService.createStudyLike(studyId, user.getId());

        //when
        studyCommandService.deleteStudyLike(studyId, user.getId());

        //then
        boolean result = studyLikeRepository.existsByStudyIdAndUserId(studyId, user.getId());
        assertFalse(result);

        Study study = studyRepository.findById(studyId).get();
        assertEquals(0, study.getLikeCount());
    }

    @Test
    @DisplayName("존재하지 않는 스터디 모집 좋아요에 대해 삭제하면 실패한다.")
    void deleteStudyLike_WhenNonExists_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.deleteStudyLike(studyId, user.getId()));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 취소 동시성 제어에 성공한다.")
    void deleteStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        List<UUID> userIds = insertUsersAndGetUserIds(userRepository, THREAD_COUNT);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        for (UUID userId : userIds) {
            studyCommandService.createStudyLike(studyId, userId);
        }

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = userIds.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.deleteStudyLike(studyId, userId);
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

        Study study = studyRepository.findById(studyId).get();
        assertEquals(0, study.getLikeCount());
    }

    @Test
    @DisplayName("스터디 모집 삭제에 성공한다.")
    void deleteStudy_Success() {
        //given
        studyCommandService.createStudyLike(studyId, user.getId());
        studyCommandService.createStudyBookmark(studyId, user.getId());

        UpsertStudyCommentRequest createRequest = UpsertStudyCommentRequestFixture.create();
        Long parentId = commentCommandService.createComment(
                createRequest,
                studyId,
                null,
                user.getId()
        ).getCommentId();
        commentCommandService.createComment(
                createRequest,
                studyId,
                parentId,
                user.getId()
        );

        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user.getId()
        );

        //when
        studyCommandService.deleteStudy(studyId, user.getId());

        //then
        assertEquals(0, studyTagRepository.count());
        assertEquals(0, studyImageRepository.count());
        assertEquals(0, recruitmentPositionRepository.count());
        assertEquals(0, studyLikeRepository.count());
        assertEquals(0, commentRepository.count());
        assertEquals(0, studyBookmarkRepository.count());
        assertEquals(0, studyRepository.count());
        assertEquals(0, participationRepository.count());
        assertEquals(0, participationLinkRepository.count());
    }

    @Test
    @DisplayName("스터디 모집 다중 삭제에 성공한다.")
    void deleteStudies_Success() {
        //given
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        Long newStudyId = 2L;
        Long newRecruitmentPositionId = 5L; // setUp에서 2개 생성했으므로 새로 생성된 모집 포지션 ID는 5부터 시작

        studyCommandService.createStudyLike(studyId, user.getId());
        studyCommandService.createStudyBookmark(studyId, user.getId());

        studyCommandService.createStudyLike(newStudyId, user.getId());
        studyCommandService.createStudyBookmark(newStudyId, user.getId());

        UpsertStudyCommentRequest createRequest = UpsertStudyCommentRequestFixture.create();
        Long parentId1 = commentCommandService.createComment(
                createRequest,
                studyId,
                null,
                user.getId()
        ).getCommentId();
        commentCommandService.createComment(
                createRequest,
                newStudyId,
                parentId1,
                user.getId()
        );

        Long parentId2 = commentCommandService.createComment(
                createRequest,
                newStudyId,
                null,
                user.getId()
        ).getCommentId();
        commentCommandService.createComment(
                createRequest,
                newStudyId,
                parentId2,
                user.getId()
        );

        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPositionId,
                user.getId()
        );

        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                newRecruitmentPositionId,
                user.getId()
        );

        //when
        studyCommandService.deleteStudies(
                DeleteStudiesRequestFixture.create(List.of(studyId, newStudyId)), user.getId()
        );

        //then
        assertEquals(0, studyTagRepository.count());
        assertEquals(0, studyImageRepository.count());
        assertEquals(0, recruitmentPositionRepository.count());
        assertEquals(0, studyLikeRepository.count());
        assertEquals(0, commentRepository.count());
        assertEquals(0, studyBookmarkRepository.count());
        assertEquals(0, studyRepository.count());
        assertEquals(0, participationRepository.count());
        assertEquals(0, participationLinkRepository.count());
    }

    @Test
    @DisplayName("삭제할 스터디가 존재하지 않으면 스터디 모집 다중 삭제에 실패한다.")
    void deleteStudies_WhenNotExist_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.deleteStudies(
                DeleteStudiesRequestFixture.create(List.of(studyId, 123456789L)), user.getId()
        ));
    }

//    @Test
//    @DisplayName("스터디 모집 포지션 변경에 성공한다.")
//    void modifyRecruitmentPosition_Success() {
//        //given
//        ModifyRecruitmentPositionRequest request = ModifyRecruitmentPositionRequestFixture.create();
//
//        //when
//        studyCommandService.modifyRecruitmentPosition(
//                request,
//                studyId,
//                user.getId()
//        );
//
//        Optional<Study>
//    }
}
