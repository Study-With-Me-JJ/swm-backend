package com.jj.swm.domain.studyroom.review.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.core.fixture.StudyRoomFixture;
import com.jj.swm.domain.studyroom.core.fixture.StudyRoomReserveTypeFixture;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomReserveTypeRepository;
import com.jj.swm.domain.studyroom.reservation.entity.StudyRoomReservationInfo;
import com.jj.swm.domain.studyroom.reservation.fixture.StudyRoomReservationInfoFixture;
import com.jj.swm.domain.studyroom.reservation.repository.StudyRoomReservationInfoRepository;
import com.jj.swm.domain.studyroom.review.dto.request.CreateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.review.dto.request.CreateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.review.dto.request.UpdateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.review.dto.request.UpdateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.review.dto.response.CreateStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.review.dto.response.CreateStudyRoomReviewReplyResponse;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.review.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.review.entity.StudyRoomReviewReply;
import com.jj.swm.domain.studyroom.review.fixture.StudyRoomReviewFixture;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.review.repository.StudyRoomReviewReplyRepository;
import com.jj.swm.domain.studyroom.review.repository.StudyRoomReviewRepository;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jj.swm.domain.user.helper.UserTestHelper.insertUsersAndGetUserIds;
import static org.assertj.core.api.Assertions.assertThat;

public class StudyRoomReviewCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    private static final int THREAD_COUNT = 50;
    private static final List<String> ignoreBeforeEachMethod = List.of(
            "createReview_ConcurrencyTest_Success",
            "deleteReview_ConcurrencyTest_Success");

    // Target Service Bean
    @Autowired private StudyRoomReviewCommandService commandService;

    // Repository Bean
    @Autowired private StudyRoomRepository studyRoomRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private StudyRoomReviewRepository reviewRepository;
    @Autowired private StudyRoomReviewReplyRepository reviewReplyRepository;
    @Autowired private StudyRoomReservationInfoRepository reservationInfoRepository;

    // Helper Repository Bean
    @Autowired private StudyRoomReserveTypeRepository reserveTypeRepository;

    private StudyRoom studyRoom;
    private User createReviewUser;
    private StudyRoomReview studyRoomReview;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        User roomAdmin = userRepository.save(UserFixture.createRoomAdmin());
        studyRoom = studyRoomRepository.save(StudyRoomFixture.create(roomAdmin));

        if(ignoreBeforeEachMethod.contains(testInfo.getTestMethod().orElseThrow().getName())) {
            return;
        }

        createReviewUser = userRepository.save(UserFixture.create());
        studyRoomReview = StudyRoomReviewFixture.create(studyRoom, 5, createReviewUser);

        reviewRepository.save(studyRoomReview);
    }

    @Test
    @DisplayName("스터디 룸 이용후기 생성에 성공한다.")
    void createReview_Success() {
        //given
        StudyRoomReserveType studyRoomReserveType = StudyRoomReserveTypeFixture.create(studyRoom);
        reserveTypeRepository.save(studyRoomReserveType);

        StudyRoomReservationInfo studyRoomReservationInfo = StudyRoomReservationInfoFixture.createApproved(
                createReviewUser, studyRoom, studyRoomReserveType
        );
        reservationInfoRepository.save(studyRoomReservationInfo);

        CreateStudyRoomReviewRequest request = CreateStudyRoomReviewRequest.builder()
                .comment("test")
                .rating(5)
                .imageUrls(List.of("image1", "image2"))
                .build();

        //when
        CreateStudyRoomReviewResponse response = commandService.createReview(
                request,
                studyRoom.getId(),
                createReviewUser.getId()
        );

        //then
        StudyRoomReview studyRoomReview = reviewRepository.findById(response.getStudyRoomReviewId()).get();
        assertThat(studyRoomReview.getComment()).isEqualTo("test");
        assertThat(studyRoomReview.getRating()).isEqualTo(5);

        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        assertThat(studyRoom.getReviewCount()).isEqualTo(1);
        assertThat(studyRoom.getAverageRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("등록되지 않은 스터디 룸 ID로 생성시 이용후기 생성에 실패한다.")
    void createReview_WhenNotValidStudyRoom_ThenFail() {
        //given
        CreateStudyRoomReviewRequest request = CreateStudyRoomReviewRequest.builder()
                .comment("test")
                .rating(5)
                .imageUrls(List.of("image1", "image2"))
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.createReview(
                        request,
                        100L,
                        createReviewUser.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 이용후기 생성 시 예약 정보가 없다면 생성에 실패한다.")
    void createReview_WhenAbsentReservationInfo_ThenFail() {
        //given
        CreateStudyRoomReviewRequest request = CreateStudyRoomReviewRequest.builder()
                .comment("test")
                .rating(5)
                .imageUrls(List.of("image1", "image2"))
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.createReview(request, studyRoom.getId(), createReviewUser.getId()));
    }

    @Test
    @DisplayName("스터디 룸 이용후기 생성 시 해당 유저의 예약 정보가 없다면 생성에 실패한다.")
    void createReview_WhenAbsentUserReservationInfo_ThenFail() {
        //given
        StudyRoomReserveType studyRoomReserveType = StudyRoomReserveTypeFixture.create(studyRoom);
        reserveTypeRepository.save(studyRoomReserveType);

        StudyRoomReservationInfo studyRoomReservationInfo = StudyRoomReservationInfoFixture.createApproved(
                createReviewUser, studyRoom, studyRoomReserveType
        );
        reservationInfoRepository.save(studyRoomReservationInfo);

        CreateStudyRoomReviewRequest request = CreateStudyRoomReviewRequest.builder()
                .comment("test")
                .rating(5)
                .imageUrls(List.of("image1", "image2"))
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.createReview(request, studyRoom.getId(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("스터디 룸 이용후기 수정에 성공한다.")
    @Transactional
    void updateReview_Success() {
        //given
        UpdateStudyRoomReviewRequest request = UpdateStudyRoomReviewRequest.builder()
                .comment("update_test")
                .rating(4)
                .build();

        //StudyRoom에 이용 후기 점수 추가
        studyRoom.addReview(5);

        //when
        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        commandService.updateReview(
                request,
                studyRoom.getId(),
                studyRoomReview.getId(),
                createReviewUser.getId()
        );

        //then
        studyRoomReview = reviewRepository.findById(studyRoomReview.getId()).get();
        assertThat(studyRoomReview.getComment()).isEqualTo("update_test");
        assertThat(studyRoomReview.getRating()).isEqualTo(4);

        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        assertThat(studyRoom.getAverageRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("스터디 룸 이용후기 작성자가 아니라면 삭제에 실패한다.")
    void updateReview_WhenNotAuthor_ThenFail() {
        //given
        UpdateStudyRoomReviewRequest request = UpdateStudyRoomReviewRequest.builder()
                .comment("update_test")
                .rating(4)
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateReview(
                        request,
                        studyRoom.getId(), studyRoomReview.getId(),
                        UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("스터디 룸 이용후기 삭제에 성공한다.")
    void deleteReview_Success() {
        //when
        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        commandService.deleteReview(studyRoom.getId(), studyRoomReview.getId(), createReviewUser.getId());

        //then
        Optional<StudyRoomReview> findStudyRoomReview = reviewRepository.findById(studyRoomReview.getId());
        assertThat(findStudyRoomReview.isPresent()).isFalse();
    }

    @Test
    @DisplayName("스터디 룸 이용후기 작성자가 아니라면 삭제에 실패한다.")
    void deleteReview_WhenNotAuthor_ThenFail() {
        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.deleteReview(studyRoom.getId(), studyRoomReview.getId(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("스터디 룸 이용후기 평균 평점 계산에 성공한다.")
    void createReview_AboutCalculateAverageRating_Success() {
        //given
        List<UUID> userUuids = insertUsersAndGetUserIds(userRepository, 5);
        int rating = 5;

        //when
        for (UUID uuid : userUuids) {
            commandService.createReview(CreateStudyRoomReviewRequest.builder()
                            .comment("test")
                            .rating(rating--)
                            .imageUrls(null)
                            .build(),
                    studyRoom.getId(), uuid);
        }

        //then
        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();

        // 5, 4, 3, 2, 1 -> 3(평균)
        assertThat(studyRoom.getAverageRating()).isEqualTo(3);
    }

    @Test
    @DisplayName("스터디 룸 이용후기를 작성한 일반 유저 혹은 관리자이면 답글 생성에 성공한다.")
    void createReviewReply_WhenNormalUserAndRoomAdmin_Success() {
        //given
        CreateStudyRoomReviewReplyRequest requestNormalUser = CreateStudyRoomReviewReplyRequest.builder()
                .reply("normalUser")
                .build();

        CreateStudyRoomReviewReplyRequest requestRoomAdmin = CreateStudyRoomReviewReplyRequest.builder()
                .reply("roomAdmin")
                .build();

        //when
        CreateStudyRoomReviewReplyResponse responseNormalUserCreateReview
                = commandService.createReviewReply(requestNormalUser, studyRoomReview.getId(), createReviewUser.getId());

        CreateStudyRoomReviewReplyResponse responseRoomAdminCreateReview
                = commandService.createReviewReply(requestRoomAdmin, studyRoomReview.getId(), studyRoom.getUser().getId());

        //then
        StudyRoomReviewReply studyRoomReviewReply
                = reviewReplyRepository.findById(responseNormalUserCreateReview.getStudyRoomReviewReplyId()).get();

        assertThat(studyRoomReviewReply.getReply()).isEqualTo("normalUser");

        studyRoomReviewReply
                = reviewReplyRepository.findById(responseRoomAdminCreateReview.getStudyRoomReviewReplyId()).get();

        assertThat(studyRoomReviewReply.getReply()).isEqualTo("roomAdmin");
    }

    @Test
    @DisplayName("스터디 룸 이용후기를 작성한 유저가 아니라면 답글 생성에 실패한다.")
    void createReviewReply_WhenNotValidUser_ThenFail() {
        //given
        CreateStudyRoomReviewReplyRequest request = CreateStudyRoomReviewReplyRequest.builder()
                .reply("test")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.createReviewReply(
                        request,
                        studyRoomReview.getId(),
                        UUID.randomUUID()
                )
         );
    }

    @Test
    @DisplayName("스터디 룸 이용후기 답글 수정에 성공한다.")
    void updateReviewReply_Success() {
        //given
        StudyRoomReviewReply studyRoomReviewReply = reviewReplyRepository.save(
                StudyRoomReviewReply.of(
                        "test",
                        studyRoomReview,
                        createReviewUser
                )
        );

        UpdateStudyRoomReviewReplyRequest request = UpdateStudyRoomReviewReplyRequest.builder()
                .reply("update_test")
                .build();

        //when
        commandService.updateReviewReply(request, studyRoomReviewReply.getId(), createReviewUser.getId());

        //then
        studyRoomReviewReply = reviewReplyRepository.findById(studyRoomReviewReply.getId()).get();
        assertThat(studyRoomReviewReply.getReply()).isEqualTo("update_test");
    }

    @Test
    @DisplayName("스터디 룸 이용후기 답글 작성자가 아니라면 수정에 실패한다.")
    void updateReviewReply_WhenNotAuthor_ThenFail() {
        //given
        StudyRoomReviewReply studyRoomReviewReply = reviewReplyRepository.save(
                StudyRoomReviewReply.of(
                        "test",
                        studyRoomReview,
                        createReviewUser
                )
        );

        UpdateStudyRoomReviewReplyRequest request = UpdateStudyRoomReviewReplyRequest.builder()
                .reply("update_test")
                .build();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateReviewReply(
                        request,
                        studyRoomReviewReply.getId(),
                        UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("스터디 룸 이용후기 답글 삭제에 성공한다.")
    void deleteReviewReply_Success() {
        //given
        StudyRoomReviewReply studyRoomReviewReply = reviewReplyRepository.save(
                StudyRoomReviewReply.of(
                        "test",
                        studyRoomReview,
                        createReviewUser
                )
        );

        //when
        commandService.deleteReviewReply(studyRoomReviewReply.getId(), createReviewUser.getId());

        //then
        Optional<StudyRoomReviewReply> findStudyRoomReviewReply = reviewReplyRepository.findById(studyRoomReviewReply.getId());
        assertThat(findStudyRoomReviewReply.isPresent()).isFalse();
    }

    @Test
    @DisplayName("스터디 룸 이용후기 답글 작성자가 아니라면 삭제에 실패한다.")
    void deleteReviewReply_WhenNotAuthor_ThenFail() {
        //given
        StudyRoomReviewReply studyRoomReviewReply = reviewReplyRepository.save(
                StudyRoomReviewReply.of(
                        "test",
                        studyRoomReview,
                        createReviewUser
                )
        );

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.deleteReviewReply(studyRoomReviewReply.getId(), UUID.randomUUID()));
    }


    @Test
    @DisplayName("스터디 룸 이용후기 동시성 테스트에 성공한다.")
    void createReview_ConcurrencyTest_Success() throws InterruptedException {
        //given
        List<UUID> userUuids = insertUsersAndGetUserIds(userRepository, THREAD_COUNT);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        CreateStudyRoomReviewRequest request = CreateStudyRoomReviewRequest.builder()
                .comment("test")
                .rating(5)
                .imageUrls(null)
                .build();

        //when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final UUID uuid = userUuids.get(i);
            executorService.submit(() -> {
                try {
                    commandService.createReview(request, studyRoom.getId(), uuid);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        executorService.shutdown();
        countDownLatch.await();

        // then
        int reviewCount = reviewRepository.countStudyRoomReviewByStudyRoom(studyRoom);
        assertThat(reviewCount).isEqualTo(THREAD_COUNT); // 사용자마다 한 번씩 이용후기가 생성되어야함

        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        assertThat(studyRoom.getReviewCount()).isEqualTo(THREAD_COUNT);
    }

    @Test
    @DisplayName("스터디 룸 이용후기 삭제 테스트에 성공한다.")
    void deleteReview_ConcurrencyTest_Success() throws InterruptedException {
        //given
        CreateStudyRoomReviewRequest request = CreateStudyRoomReviewRequest.builder()
                .comment("test")
                .rating(5)
                .build();

        createReviewUser = userRepository.save(UserFixture.create());

        CreateStudyRoomReviewResponse response
                = commandService.createReview(request, studyRoom.getId(), createReviewUser.getId());

        //when
        commandService.deleteReview(studyRoom.getId(), response.getStudyRoomReviewId(), createReviewUser.getId());

        //then
        Optional<StudyRoomReview> findStudyRoomReview = reviewRepository.findById(response.getStudyRoomReviewId());

        assertThat(findStudyRoomReview.isPresent()).isFalse();
    }
}
