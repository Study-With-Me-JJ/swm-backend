package com.jj.swm.domain.studyroom.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.StudyRoomFixture;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomLikeCreateResponse;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.repository.StudyRoomLikeRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomRepository;
import com.jj.swm.domain.user.UserFixture;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class StudyRoomCommandServiceTest extends IntegrationContainerSupporter {

    private static final int THREAD_COUNT = 100;

    @Autowired
    private StudyRoomCommandService studyRoomCommandService;

    @Autowired
    private StudyRoomLikeRepository likeRepository;

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private UserRepository userRepository;

    private StudyRoom studyRoom;
    private List<UUID> userUuids;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp(){
        User user = userRepository.saveAndFlush(UserFixture.createUser());
        studyRoom = studyRoomRepository.saveAndFlush(StudyRoomFixture.createStudyRoomWithoutId(user));
        userUuids = createTestUsers();
    }

    @Test
    @DisplayName("스터디 룸 좋아요 동시성 테스트에 성공한다.")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void studyRoom_like_concurrency_test() throws InterruptedException {
        //given
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        // when
        for(int i = 0; i < THREAD_COUNT; i++) {
            final UUID uuid = userUuids.get(i);
            executorService.submit(() -> {
                try {
                    studyRoomCommandService.createLike(studyRoom.getId(), uuid);
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        executorService.shutdown();
        countDownLatch.await();

        // then
        long likeCount = likeRepository.countStudyRoomLikeByStudyRoom(studyRoom);
        assertThat(likeCount).isEqualTo(100); // 사용자마다 한 번씩 좋아요가 생성되어야 함

        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        assertThat(studyRoom.getLikeCount()).isEqualTo(100);
    }

    @Test
    @DisplayName("스터디 룸 좋아요 취소 동시성 테스트에 성공한다.")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void studyRoom_dislike_concurrency_test() throws InterruptedException {
        //given
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        List<Long> studyRoomLikeIds = new ArrayList<>();

        for(UUID uuid : userUuids) {
           StudyRoomLikeCreateResponse response = studyRoomCommandService.createLike(studyRoom.getId(), uuid);
           studyRoomLikeIds.add(response.getStudyRoomLikeId());
        }

        //when
        for(int i = 0; i < THREAD_COUNT; i++) {
            final UUID uuid = userUuids.get(i);
            int idx = i;
            executorService.submit(() -> {
                try {
                    studyRoomCommandService.deleteLike(
                            studyRoom.getId(),
                            studyRoomLikeIds.get(idx),
                            uuid
                    );
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        executorService.shutdown();
        countDownLatch.await();

        // then
        long likeCount = likeRepository.countStudyRoomLikeByStudyRoom(studyRoom);
        assertThat(likeCount).isEqualTo(0); // 최종적으로 좋아요가 모두 취소되어야 함

        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        assertThat(studyRoom.getLikeCount()).isEqualTo(0);
    }

    private List<UUID> createTestUsers() {
        List<UUID> userUuids = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            User user = UserFixture.createUserWithUUID();
            user = userRepository.save(user);
            userUuids.add(user.getId());
        }
        return userUuids;
    }
}

