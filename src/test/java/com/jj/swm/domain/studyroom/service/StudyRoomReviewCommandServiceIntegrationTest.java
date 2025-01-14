package com.jj.swm.domain.studyroom.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.fixture.StudyRoomFixture;
import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomReviewReplyResponse;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.entity.StudyRoomReviewReply;
import com.jj.swm.domain.studyroom.fixture.StudyRoomReviewFixture;
import com.jj.swm.domain.studyroom.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomReviewReplyRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomReviewRepository;
import com.jj.swm.domain.user.fixture.UserFixture;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup/studyroom.sql")
public class StudyRoomReviewCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    private static final int THREAD_COUNT = 50;
    private static final List<String> ignoreBeforeEachMethod = List.of(
            "studyRoom_review_concurrency_test_Success",
            "studyRoom_deleteReview_concurrency_test_Success");

    // Target Service Bean
    @Autowired private StudyRoomReviewCommandService commandService;

    // Repository Bean
    @Autowired private StudyRoomRepository studyRoomRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private StudyRoomReviewRepository reviewRepository;
    @Autowired private StudyRoomReviewReplyRepository reviewReplyRepository;

    private StudyRoom studyRoom;
    private User createReviewUser;
    private StudyRoomReview studyRoomReview;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        User roomAdmin = userRepository.saveAndFlush(UserFixture.createRoomAdmin());
        studyRoom = studyRoomRepository.saveAndFlush(StudyRoomFixture.createStudyRoom(roomAdmin));

        if(ignoreBeforeEachMethod.contains(testInfo.getTestMethod().orElseThrow().getName())) {
            return;
        }

        createReviewUser = userRepository.saveAndFlush(UserFixture.createUserWithUUID());
        studyRoomReview = StudyRoomReviewFixture.createReview(studyRoom, 5, createReviewUser);

        reviewRepository.saveAndFlush(studyRoomReview);
    }

    @Test
    @DisplayName("스터디 룸 이용후기 생성에 성공한다.")
    void studyRoom_createReview_Success() {
        //given
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
    void studyRoom_createReview_whenNotValidStudyRoom_Fail() {
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
    @DisplayName("스터디 룸 이용후기 수정에 성공한다.")
    void studyRoom_updateReview_Success() {
        //given
        UpdateStudyRoomReviewRequest request = UpdateStudyRoomReviewRequest.builder()
                .comment("update_test")
                .rating(4)
                .build();

        //StudyRoom에 이용 후기 점수 추가
        studyRoom.addReviewStudyRoom(5);

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
    void studyRoom_updateReview_whenNotAuthor_Fail() {
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
    void studyRoom_deleteReview_Success() {
        //when
        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        commandService.deleteReview(studyRoom.getId(), studyRoomReview.getId(), createReviewUser.getId());

        //then
        Optional<StudyRoomReview> findStudyRoomReview = reviewRepository.findById(studyRoomReview.getId());
        assertThat(findStudyRoomReview.isPresent()).isFalse();
    }

    @Test
    @DisplayName("스터디 룸 이용후기 작성자가 아니라면 삭제에 실패한다.")
    void studyRoom_deleteReview_whenNotAuthor_Fail() {
        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.deleteReview(studyRoom.getId(), studyRoomReview.getId(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("스터디 룸 이용후기 평균 평점 계산에 성공한다.")
    void studyRoom_calculate_average_rating_Success() {
        //given
        List<UUID> userUuids = createTestUsers(5);
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
    void studyRoom_review_create_normal_user_and_room_admin_Success() {
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
    void studyRoom_review_create_unknown_user_thenFail() {
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
    void studyRoom_review_reply_update_Success() {
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
    void studyRoom_updateReviewReply_whenNotAuthor_thenFail() {
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
    void studyRoom_deleteReviewReply_Success() {
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
    void studyRoom_deleteReviewReply_whenNotAuthor_thenFail() {
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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void studyRoom_review_concurrency_test_Success() throws InterruptedException {
        //given
        List<UUID> userUuids = createTestUsers(THREAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);

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
    @DisplayName("스터디 룸 이용후기 삭제 동시성 테스트에 성공한다.")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void studyRoom_deleteReview_concurrency_test_Success() throws InterruptedException {
        //given
        List<UUID> userUuids = createTestUsers(THREAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);

        for(UUID uuid : userUuids) {
            CreateStudyRoomReviewRequest request = CreateStudyRoomReviewRequest.builder()
                    .comment("test")
                    .rating(5)
                    .build();

            commandService.createReview(request, studyRoom.getId(), uuid);
        }

        //when
        for(int i = 0; i < THREAD_COUNT; i++) {
            final UUID uuid = userUuids.get(i);
            int finalI = i;
            executorService.submit(() -> {
                try {
                    commandService.deleteReview(studyRoom.getId(), (long) (finalI + 1), uuid);
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        executorService.shutdown();
        countDownLatch.await();

        //then
        StudyRoom findStudyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        assertThat(findStudyRoom.getReviewCount()).isEqualTo(0);
        assertThat(findStudyRoom.getAverageRating()).isEqualTo(0);
    }

    private List<UUID> createTestUsers(int size) {
        List<User> users = UserFixture.multiUser(size);

        userRepository.saveAll(users);

        return users.stream().map(User::getId).toList();
    }
}
