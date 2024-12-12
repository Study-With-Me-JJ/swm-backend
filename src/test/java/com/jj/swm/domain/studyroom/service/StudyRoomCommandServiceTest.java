package com.jj.swm.domain.studyroom.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.StudyRoomFixture;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.repository.StudyRoomLikeRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomRepository;
import com.jj.swm.domain.user.UserFixture;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
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

    @Autowired private StudyRoomCommandService commandService;
    @Autowired private StudyRoomLikeRepository likeRepository;
    @Autowired private StudyRoomRepository studyRoomRepository;
    @Autowired private UserRepository userRepository;

    private StudyRoom studyRoom;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp(){
        User user = userRepository.saveAndFlush(UserFixture.createRoomAdmin());
        studyRoom = studyRoomRepository.saveAndFlush(StudyRoomFixture.createStudyRoomWithoutId(user));
    }

    @AfterEach
    void cleanup() {
        likeRepository.deleteAllInBatch();
        studyRoomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("스터디 룸 좋아요 동시성 테스트에 성공한다.")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void studyRoom_like_concurrency_test_Success() throws InterruptedException {
        //given
        List<UUID> userUuids = createTestUsers(THREAD_COUNT);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        // when
        for(int i = 0; i < THREAD_COUNT; i++) {
            final UUID uuid = userUuids.get(i);
            executorService.submit(() -> {
                try {
                    commandService.createLike(studyRoom.getId(), uuid);
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
        int likeCount = likeRepository.countStudyRoomLikeByStudyRoom(studyRoom);
        assertThat(likeCount).isEqualTo(100); // 사용자마다 한 번씩 좋아요가 생성되어야 함

        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        assertThat(studyRoom.getLikeCount()).isEqualTo(100);
    }

    @Test
    @DisplayName("스터디 룸 좋아요 취소 동시성 테스트에 성공한다.")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void studyRoom_dislike_concurrency_test_Success() throws InterruptedException {
        //given
        List<UUID> userUuids = createTestUsers(THREAD_COUNT);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        for(UUID uuid : userUuids) {
           commandService.createLike(studyRoom.getId(), uuid);
        }

        //when
        for(int i = 0; i < THREAD_COUNT; i++) {
            final UUID uuid = userUuids.get(i);
            executorService.submit(() -> {
                try {
                    commandService.unLike(studyRoom.getId(), uuid);
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
        int likeCount = likeRepository.countStudyRoomLikeByStudyRoom(studyRoom);
        assertThat(likeCount).isEqualTo(0); // 최종적으로 좋아요가 모두 취소되어야 함

        studyRoom = studyRoomRepository.findById(studyRoom.getId()).get();
        assertThat(studyRoom.getLikeCount()).isEqualTo(0);
    }

    private List<UUID> createTestUsers(int size) {
        List<UUID> userUuids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            User user = UserFixture.createUserWithUUID();
            user = userRepository.save(user);
            userUuids.add(user.getId());
        }
        return userUuids;
    }
}

