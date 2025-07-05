package com.jj.swm.domain.study.core.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.comment.dto.request.UpsertStudyCommentRequest;
import com.jj.swm.domain.study.comment.fixture.dto.request.UpsertStudyCommentRequestFixture;
import com.jj.swm.domain.study.comment.repository.StudyCommentRepository;
import com.jj.swm.domain.study.comment.service.StudyCommentCommandService;
import com.jj.swm.domain.study.core.dto.request.CreateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.ModifyRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.dto.request.ModifyRecruitmentPositionRequest.UpdateRecruitmentPositionInfo;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyStatusRequest;
import com.jj.swm.domain.study.core.entity.*;
import com.jj.swm.domain.study.core.fixture.dto.request.*;
import com.jj.swm.domain.study.core.fixture.entity.StudyFixture;
import com.jj.swm.domain.study.core.repository.StudyBookmarkRepository;
import com.jj.swm.domain.study.core.repository.StudyImageRepository;
import com.jj.swm.domain.study.core.repository.StudyLikeRepository;
import com.jj.swm.domain.study.core.repository.StudyTagRepository;
import com.jj.swm.domain.study.core.support.RecruitmentPositionTestRepository;
import com.jj.swm.domain.study.core.support.StudyImageTestRepository;
import com.jj.swm.domain.study.core.support.StudyTagTestRepository;
import com.jj.swm.domain.study.core.support.StudyTestRepository;
import com.jj.swm.domain.study.participation.entity.StudyParticipation;
import com.jj.swm.domain.study.participation.fixture.dto.request.CreateStudyParticipationRequestFixture;
import com.jj.swm.domain.study.participation.fixture.dto.request.UpdateStudyParticipationStatusRequestFixture;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.service.StudyParticipationCommandService;
import com.jj.swm.domain.study.participation.support.StudyParticipationTestRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jj.swm.domain.study.participation.entity.StudyParticipation.StudyParticipationStatus.ACCEPTED;
import static com.jj.swm.domain.study.support.TestConstants.NON_EXISTING_ID;
import static com.jj.swm.domain.study.support.TestConstants.THREAD_COUNT;
import static com.jj.swm.domain.user.helper.UserTestHelper.insertUsersAndGetUserIds;
import static org.junit.jupiter.api.Assertions.*;

class StudyCommandServiceIntegrationTest extends IntegrationContainerSupporter {

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
    private StudyTestRepository studyTestRepository;

    @Autowired
    private StudyImageRepository studyImageRepository;

    @Autowired
    private StudyImageTestRepository studyImageTestRepository;

    @Autowired
    private StudyTagRepository studyTagRepository;

    @Autowired
    private StudyTagTestRepository studyTagTestRepository;

    @Autowired
    private StudyBookmarkRepository studyBookmarkRepository;

    @Autowired
    private StudyLikeRepository studyLikeRepository;

    @Autowired
    private RecruitmentPositionTestRepository recruitmentPositionTestRepository;

    @Autowired
    private StudyCommentRepository commentRepository;

    @Autowired
    private StudyParticipationTestRepository participationTestRepository;

    @Autowired
    private StudyParticipationLinkRepository participationLinkRepository;

    // entity
    private User user;
    private Study study;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.create());

        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        study = studyTestRepository.findFirstByOrderById().orElseThrow();
    }

    @Test
    @DisplayName("스터디 모집 생성에 성공한다.")
    void createStudy_Success() {
        //given
        CreateStudyRequest request = CreateStudyRequestFixture.create();

        //when
        studyCommandService.createStudy(request, user.getId());

        //then
        Optional<Study> optionalNewStudy = studyTestRepository.findFirstByOrderByIdDesc();
        assertTrue(optionalNewStudy.isPresent());

        Study newStudy = optionalNewStudy.get();
        assertEquals(request.getTitle(), newStudy.getTitle());
        assertEquals(request.getCategory(), newStudy.getCategory());
        assertEquals(request.getOpenChatUrl(), newStudy.getOpenChatUrl());
        assertEquals(request.getCategory(), newStudy.getCategory());

        List<String> tagNames = studyTagRepository.findAllByStudyId(newStudy.getId()).stream()
                .map(StudyTag::getName)
                .toList();
        assertTrue(tagNames.containsAll(request.getTags()));

        List<String> imageUrls = studyImageRepository.findAllByStudyId(newStudy.getId()).stream()
                .map(StudyImage::getImageUrl)
                .toList();
        assertTrue(imageUrls.containsAll(request.getImageUrls()));

        assertEquals(
                request.getRecruitmentPositionInfos().size(),
                recruitmentPositionTestRepository.countByStudyId(newStudy.getId())
        );
    }

    @Test
    @DisplayName("tags&imageUrls가 없어도 스터디 모집 생성에 성공한다.")
    void createStudy_WithoutTagAndImages_Success() {
        //when
        studyCommandService.createStudy(CreateStudyRequestFixture.createForNoTagsAndImagesSuccess(), user.getId());

        //then
        Optional<Study> optionalNewStudy = studyTestRepository.findFirstByOrderByIdDesc();
        assertTrue(optionalNewStudy.isPresent());
    }

    @Test
    @DisplayName("스터디 모집 수정에 성공한다")
    void updateStudy_Success() {
        //given
        UpdateStudyRequest request = UpdateStudyRequestFixture.create();

        long oldTagSize = studyTagTestRepository.countByStudyId(study.getId());
        long oldImageUrlSize = studyImageTestRepository.countByStudyId(study.getId());

        //when
        studyCommandService.updateStudy(
                request,
                study.getId(),
                user.getId()
        );

        //then
        Study updatedStudy = studyTestRepository.findById(study.getId()).orElseThrow();
        assertEquals(request.getTitle(), updatedStudy.getTitle());
        assertEquals(request.getContent(), updatedStudy.getContent());
        assertEquals(request.getCategory(), updatedStudy.getCategory());
        assertEquals(request.getOpenChatUrl(), updatedStudy.getOpenChatUrl());

        assertEquals(
                oldTagSize + request.getModifyTagInfo().getTagsToAdd().size()
                        - request.getModifyTagInfo().getTagIdsToRemove().size(),
                studyTagTestRepository.countByStudyId(updatedStudy.getId())
        );
        assertEquals(
                oldImageUrlSize + request.getModifyImageInfo().getImageUrlsToAdd().size()
                        - request.getModifyImageInfo().getImageIdsToRemove().size(),
                studyImageTestRepository.countByStudyId(updatedStudy.getId())
        );
    }

    @Test
    @DisplayName("modifyTag&ImageInfo가 없어도 스터디 모집 수정에 성공한다.")
    void updateStudy_WithoutModifyTagAndImageInfo_Success() {
        //when & then
        assertDoesNotThrow(() -> studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForNoModifyTagAndImageInfoSuccess(),
                study.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("Tags&ImagesToAdd가 없어도 스터디 모집 수정에 성공한다.")
    void updateStudy_WithoutTagAndImagesToAdd_Success() {
        //given
        UpdateStudyRequest request = UpdateStudyRequestFixture.createForNoTagAndImagesToAddSuccess();

        long oldTagSize = studyTagTestRepository.countByStudyId(study.getId());
        long oldImageUrlSize = studyImageTestRepository.countByStudyId(study.getId());

        //when
        studyCommandService.updateStudy(
                request,
                study.getId(),
                user.getId()
        );

        //then
        assertEquals(
                oldTagSize - request.getModifyTagInfo().getTagIdsToRemove().size(),
                studyTagTestRepository.countByStudyId(study.getId())
        );
        assertEquals(
                oldImageUrlSize - request.getModifyImageInfo().getImageIdsToRemove().size(),
                studyImageTestRepository.countByStudyId(study.getId())
        );
    }

    @Test
    @DisplayName("Tag&ImageIdsToRemove가 없어도 스터디 모집 수정에 성공한다.")
    void updateStudy_WithoutTagAndImageIdsToRemove_Success() {
        //given
        UpdateStudyRequest request = UpdateStudyRequestFixture.createFroNoTagAndImageIdsToRemoveSuccess();

        long oldTagSize = studyTagTestRepository.countByStudyId(study.getId());
        long oldImageUrlSize = studyImageTestRepository.countByStudyId(study.getId());

        //when
        studyCommandService.updateStudy(
                request,
                study.getId(),
                user.getId()
        );

        //then
        assertEquals(
                oldTagSize + request.getModifyTagInfo().getTagsToAdd().size(),
                studyTagTestRepository.countByStudyId(study.getId())
        );
        assertEquals(
                oldImageUrlSize + request.getModifyImageInfo().getImageUrlsToAdd().size(),
                studyImageTestRepository.countByStudyId(study.getId())
        );
    }

    @Test
    @DisplayName("tagIdsToRemove에 옳지 않은 id값이 전달되면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenUnderTagLimit_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForWrongTagIdToRemove(),
                study.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("태그 제한 개수를 초과하면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenWrongTagIdToRemove_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForExceedTagLimitFail(),
                study.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("imageIdsToRemove에 옳지 않은 id값이 전달되면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenWrongImageIdToRemove_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForWrongImageIdToRemove(),
                study.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("이미지 제한 개수를 초과하면 스터디 모집 수정에 실패한다.")
    void updateStudy_WhenExceedImageLimit_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.updateStudy(
                UpdateStudyRequestFixture.createForExceedImageLimitFail(),
                study.getId(),
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
                study.getId(),
                user.getId()
        );

        //then
        Study updatedStudy = studyTestRepository.findById(study.getId()).orElseThrow();
        assertEquals(request.getStatus(), updatedStudy.getStatus());
    }

    @Test
    @DisplayName("스터디 모집 북마크 생성에 성공한다.")
    void createStudyBookmark_Success() {
        //when
        Long bookmarkId = studyCommandService.createStudyBookmark(study.getId(), user.getId())
                .getBookmarkId();

        //then
        Optional<StudyBookmark> optionalStudyBookmark = studyBookmarkRepository.findById(bookmarkId);
        assertTrue(optionalStudyBookmark.isPresent());
    }

    @Test
    @DisplayName("다시 북마크 생성하면 실패한다.")
    void createStudyBookmark_WhenAlreadyExists_ThenFail() {
        //given
        studyCommandService.createStudyBookmark(study.getId(), user.getId());

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.createStudyBookmark(study.getId(), user.getId()));
    }

    @Test
    @DisplayName("스터디 모집 북마크 삭제에 성공한다.")
    void deleteStudyBookmark_Success() {
        //given
        Long bookmarkId = studyCommandService.createStudyBookmark(study.getId(), user.getId())
                .getBookmarkId();

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
        assertThrows(GlobalException.class, () -> studyCommandService.deleteStudyBookmark(NON_EXISTING_ID, user.getId()));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 생성에 성공한다.")
    void createStudyLike_Success() {
        //given
        int oldLikeCount = study.getStatistics().getLikeCount();

        //when
        Long likeId = studyCommandService.createStudyLike(study.getId(), user.getId())
                .getLikeId();

        //then
        Optional<StudyLike> optionalStudyLike = studyLikeRepository.findById(likeId);
        assertTrue(optionalStudyLike.isPresent());

        Study updatedStudy = studyTestRepository.findById(study.getId()).orElseThrow();
        assertEquals(oldLikeCount + 1, updatedStudy.getStatistics().getLikeCount());
    }

    @Test
    @DisplayName("다시 좋아요 생성하면 실패한다.")
    void createStudyLike_WhenAlreadyExists_ThenFail() {
        //given
        studyCommandService.createStudyLike(study.getId(), user.getId());

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.createStudyLike(study.getId(), user.getId()));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 동시성 제어에 성공한다.")
    void createStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        int oldLikeCount = study.getStatistics().getLikeCount();

        List<UUID> userIds = insertUsersAndGetUserIds(userRepository, THREAD_COUNT);

        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = userIds.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.createStudyLike(study.getId(), userId);
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
        assertEquals(oldLikeCount + THREAD_COUNT, studyLikeRepository.count());

        Study updatedStudy = studyTestRepository.findById(study.getId()).orElseThrow();
        assertEquals(oldLikeCount + THREAD_COUNT, updatedStudy.getStatistics().getLikeCount());
    }

    @Test
    @DisplayName("스터디 모집 좋아요 삭제에 성공한다.")
    void deleteStudyLike_Success() {
        //given
        Long likeId = studyCommandService.createStudyLike(study.getId(), user.getId()).getLikeId();

        Study updatedStudy = studyTestRepository.findById(study.getId()).orElseThrow();
        int oldLikeCount = updatedStudy.getStatistics().getLikeCount();

        //when
        studyCommandService.deleteStudyLike(likeId, user.getId());

        //then
        Optional<StudyLike> optionalStudyLike = studyLikeRepository.findById(likeId);
        assertFalse(optionalStudyLike.isPresent());

        Study refetchedStudy = studyTestRepository.findById(study.getId()).orElseThrow();
        assertEquals(oldLikeCount - 1, refetchedStudy.getStatistics().getLikeCount());
    }

    @Test
    @DisplayName("존재하지 않는 스터디 모집 좋아요에 대해 삭제하면 실패한다.")
    void deleteStudyLike_WhenNonExists_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.deleteStudyLike(NON_EXISTING_ID, user.getId()));
    }

    @Test
    @DisplayName("스터디 모집 좋아요 취소 동시성 제어에 성공한다.")
    void deleteStudyLike_Concurrency_Success() throws InterruptedException {
        //given
        int oldLikeCount = study.getStatistics().getLikeCount();

        List<UUID> userIds = insertUsersAndGetUserIds(userRepository, THREAD_COUNT);

        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        List<Long> likeIds = new ArrayList<>();

        for (UUID userId : userIds) {
            Long likeId = studyCommandService.createStudyLike(study.getId(), userId).getLikeId();
            likeIds.add(likeId);
            oldLikeCount++;
        }

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = userIds.get(i);
            Long likeId = likeIds.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.deleteStudyLike(likeId, userId);
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
        assertEquals(likeIds.size() - THREAD_COUNT, studyLikeRepository.count());

        Study updatedStudy = studyTestRepository.findById(study.getId()).orElseThrow();
        assertEquals(oldLikeCount - THREAD_COUNT, updatedStudy.getStatistics().getLikeCount());
    }

    @Test
    @DisplayName("스터디 모집 다중 삭제에 성공한다.")
    void deleteStudies_Success() {
        //given
        studyCommandService.createStudy(CreateStudyRequestFixture.create(), user.getId());
        Study newStudy = studyTestRepository.findFirstByOrderByIdDesc().orElseThrow();

        StudyRecruitmentPosition recruitmentPosition = recruitmentPositionTestRepository.findFirstByStudyId(study.getId())
                .orElseThrow();
        StudyRecruitmentPosition newRecruitmentPosition =
                recruitmentPositionTestRepository.findFirstByStudyId(newStudy.getId()).orElseThrow();

        studyCommandService.createStudyLike(study.getId(), user.getId());
        studyCommandService.createStudyLike(newStudy.getId(), user.getId());

        studyCommandService.createStudyBookmark(study.getId(), user.getId());
        studyCommandService.createStudyBookmark(newStudy.getId(), user.getId());

        UpsertStudyCommentRequest createRequest = UpsertStudyCommentRequestFixture.create();
        Long parentId1 = commentCommandService.createComment(
                createRequest,
                study.getId(),
                null,
                user.getId()
        ).getCommentId();
        commentCommandService.createComment(
                createRequest,
                study.getId(),
                parentId1,
                user.getId()
        );

        Long parentId2 = commentCommandService.createComment(
                createRequest,
                newStudy.getId(),
                null,
                user.getId()
        ).getCommentId();
        commentCommandService.createComment(
                createRequest,
                newStudy.getId(),
                parentId2,
                user.getId()
        );

        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                recruitmentPosition.getId(),
                user.getId()
        );

        participationCommandService.createStudyParticipation(
                CreateStudyParticipationRequestFixture.create(),
                newRecruitmentPosition.getId(),
                user.getId()
        );

        //when
        studyCommandService.deleteStudies(
                DeleteStudyRequestFixture.create(List.of(study.getId(), newStudy.getId())), user.getId()
        );

        //then
        assertEquals(0, studyTagTestRepository.count());
        assertEquals(0, studyImageTestRepository.count());
        assertEquals(0, recruitmentPositionTestRepository.count());
        assertEquals(0, studyLikeRepository.count());
        assertEquals(0, commentRepository.count());
        assertEquals(0, studyBookmarkRepository.count());
        assertEquals(0, studyTestRepository.count());
        assertEquals(0, participationTestRepository.count());
        assertEquals(0, participationLinkRepository.count());
    }

    @Test
    @DisplayName("삭제할 스터디가 존재하는 스터디와 일치하지 않으면 스터디 모집 다중 삭제에 실패한다.")
    void deleteStudies_WhenNotExist_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.deleteStudies(
                DeleteStudyRequestFixture.create(List.of(study.getId(), NON_EXISTING_ID)), user.getId()
        ));
    }

    @Test
    @DisplayName("스터디 모집 포지션 변경에 성공한다.")
    void modifyRecruitmentPosition_Success() {
        //given
        ModifyRecruitmentPositionRequest request = ModifyRecruitmentPositionRequestFixture.create();

        long oldRecruitmentPositionSize = recruitmentPositionTestRepository.count();

        //when
        studyCommandService.modifyRecruitmentPosition(
                request,
                study.getId(),
                user.getId()
        );

        //then
        Optional<StudyRecruitmentPosition> optionalRecruitmentPosition =
                recruitmentPositionTestRepository.findById(request.getRecruitmentPositionIdsToRemove().getFirst());
        assertFalse(optionalRecruitmentPosition.isPresent());

        UpdateRecruitmentPositionInfo firstUpdateInfo = request.getRecruitmentPositionInfosToEdit().getFirst();
        StudyRecruitmentPosition recruitmentPosition =
                recruitmentPositionTestRepository.findById(firstUpdateInfo.getRecruitmentPositionId()).orElseThrow();
        assertEquals(firstUpdateInfo.getTitle(), recruitmentPosition.getTitle());

        assertEquals(
                oldRecruitmentPositionSize + request.getRecruitmentPositionInfosToAdd().size()
                        - request.getRecruitmentPositionIdsToRemove().size(),
                recruitmentPositionTestRepository.count()
        );
    }

    @Test
    @DisplayName("추가, 삭제, 수정 모집 포지션 리스트가 비어도 스터디 모집 포지션 변경에 성공한다.")
    void modifyRecruitmentPosition_WhenCreateRecruitmentPositionInfosNull_Success() {
        //given
        long oldRecruitmentPositionSize = recruitmentPositionTestRepository.count();

        //when
        studyCommandService.modifyRecruitmentPosition(
                ModifyRecruitmentPositionRequestFixture.createForModifyRecruitmentPositionRequestEmptySuccess(),
                study.getId(),
                user.getId()
        );

        //then
        assertEquals(oldRecruitmentPositionSize, recruitmentPositionTestRepository.count());
    }

    @Test
    @DisplayName("수정할 모집 포지션이 조회한 모집 포지션에 없으면 모집 포지션 변경에 실패한다.")
    void modifyRecruitmentPosition_WhenNotEqualsEditSize_ThenFail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyRecruitmentPosition(
                ModifyRecruitmentPositionRequestFixture.createForNotEqualsUpdateSizeFail(),
                study.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("수정할 모집 포지션의 모집 인원보다 승인 수가 크면 모집 포지션 변경에 실패한다.")
    void modifyRecruitmentPosition_WhenHeadcountLessThenAcceptedCount_ThenFail() {
        //given
        StudyRecruitmentPosition recruitmentPosition =
                recruitmentPositionTestRepository.findFirstByStudyId(study.getId()).orElseThrow();

        for (int i = 0; i < recruitmentPosition.getHeadcount(); i++) {
            User newUser = userRepository.save(UserFixture.create());
            participationCommandService.createStudyParticipation(
                    CreateStudyParticipationRequestFixture.create(),
                    recruitmentPosition.getId(),
                    newUser.getId()
            );
        }

        List<StudyParticipation> participations = participationTestRepository.findAllByRecruitmentPositionId(
                recruitmentPosition.getId()
        );
        for (StudyParticipation participation : participations) {
            participationCommandService.updateStudyParticipationStatus(
                    UpdateStudyParticipationStatusRequestFixture.create(ACCEPTED),
                    participation.getId(),
                    user.getId()
            );
        }

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyRecruitmentPosition(
                ModifyRecruitmentPositionRequestFixture.createForHeadcountLessThenAcceptedCountFail(
                        recruitmentPosition.getId()
                ),
                study.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("존재할 수 없는 스터디 모집이면 모집 포지션 변경에 실패한다.")
    void modifyRecruitmentPosition_WhenStudyNotExists_Fail() {
        //given
        studyTestRepository.save(StudyFixture.createForNoRecruitmentPosition(user)); // DB로 바로 저장
        Study newStudy = studyTestRepository.findFirstByOrderByIdDesc().orElseThrow();

        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyRecruitmentPosition(
                ModifyRecruitmentPositionRequestFixture.create(),
                newStudy.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("삭제할 모집 포지션이 조회한 모집 포지션에 없으면 모집 포지션 변경에 실패한다.")
    void modifyRecruitmentPosition_WhenNotEqualsRemoveSize_Fail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyRecruitmentPosition(
                ModifyRecruitmentPositionRequestFixture.createForNotEqualsDeleteSizeFail(),
                study.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("모집 포지션 최대 개수를 넘으면 모집 포지션 변경에 실패한다.")
    void modifyRecruitmentPosition_WhenExceedSize_Fail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyRecruitmentPosition(
                ModifyRecruitmentPositionRequestFixture.createForExceedSizeFail(),
                study.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("모집 포지션 최소 개수보다 작으면 모집 포지션 변경에 실패한다.")
    void modifyRecruitmentPosition_WhenUnderSize_Fail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyRecruitmentPosition(
                ModifyRecruitmentPositionRequestFixture.createForUnderSizeFail(),
                study.getId(),
                user.getId()
        ));
    }

    @Test
    @DisplayName("수정 모집 포지션이 삭제 모집 포지션과 겹치면 모집 포지션 변경에 실패한다.")
    void modifyRecruitmentPosition_WhenEditOverlapRemove_Fail() {
        //when & then
        assertThrows(GlobalException.class, () -> studyCommandService.modifyRecruitmentPosition(
                ModifyRecruitmentPositionRequestFixture.createForEditOverlapRemoveFail(),
                study.getId(),
                user.getId()
        ));
    }
}
