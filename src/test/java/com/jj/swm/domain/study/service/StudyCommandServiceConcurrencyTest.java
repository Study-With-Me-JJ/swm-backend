package com.jj.swm.domain.study.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.study.StudyFixture;
import com.jj.swm.domain.study.StudyLikeFixture;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.repository.StudyLikeRepository;
import com.jj.swm.domain.study.repository.StudyRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudyCommandServiceConcurrencyTest extends IntegrationContainerSupporter {

    private final int NUMBER_OF_THREADS = 100;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyLikeRepository studyLikeRepository;

    @Autowired
    private StudyCommandService studyCommandService;

    private CountDownLatch countDownLatch;
    private ExecutorService executorService;

    private Study study;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(UserFixture.createUser());
        study = studyRepository.save(StudyFixture.createStudy(user));
    }

    @AfterEach
    void cleanUp() {
        studyLikeRepository.deleteAllInBatch();
        studyRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("동시에 100개 좋아요 요청 테스트에 성공한다.")
    void likeStudy_Success() throws InterruptedException {
        //given
        executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        countDownLatch = new CountDownLatch(NUMBER_OF_THREADS);

        List<UUID> userIds = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            User user = userRepository.save(UserFixture.createUser());
            userIds.add(user.getId());
        }

        //when
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            UUID userId = userIds.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.likeStudy(userId, study.getId());
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
        assertEquals(100, studyRepository.findById(study.getId()).get().getLikeCount());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("동시에 100개 좋아요 취소 요청 테스트에 성공한다.")
    void unLikeStudy_Success() throws InterruptedException {
        //given
        executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        countDownLatch = new CountDownLatch(NUMBER_OF_THREADS);

        List<User> users = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            User user = userRepository.save(UserFixture.createUserWithUUID());
            users.add(user);
        }

        for (User user : users) {
            studyLikeRepository.save(StudyLikeFixture.createStudyLike(user, study));
        }

        List<UUID> userIds = users.stream().map(User::getId).toList();

        //when
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            UUID userId = userIds.get(i);
            executorService.submit(() -> {
                try {
                    studyCommandService.unLikeStudy(userId, study.getId());
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
        assertEquals(0, studyRepository.findById(study.getId()).get().getLikeCount());
    }
}
