package com.jj.swm.domain.studyroom.core.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.core.repository.*;
import com.jj.swm.domain.studyroom.core.dto.request.CreateStudyRoomRequest;
import com.jj.swm.domain.studyroom.core.dto.request.UpdateStudyRoomRequest;
import com.jj.swm.domain.studyroom.core.dto.request.UpdateStudyRoomSettingRequest;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
import com.jj.swm.domain.studyroom.core.fixture.StudyRoomFixture;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class StudyRoomCommandServiceIntegrationTest extends IntegrationContainerSupporter {

    private static final int THREAD_COUNT = 100;

    // Service Bean
    @Autowired private StudyRoomCommandService commandService;

    // Repository Bean
    @Autowired private UserRepository userRepository;
    @Autowired private StudyRoomRepository studyRoomRepository;
    @Autowired private StudyRoomImageRepository imageRepository;
    @Autowired private StudyRoomOptionInfoRepository optionInfoRepository;
    @Autowired private StudyRoomReserveTypeRepository reserveTypeRepository;
    @Autowired private StudyRoomLikeRepository likeRepository;
    @Autowired private StudyRoomBookmarkRepository bookmarkRepository;

    private StudyRoom studyRoom;
    private User roomAdmin;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp(){
        roomAdmin = userRepository.saveAndFlush(UserFixture.createRoomAdmin());
        commandService.create(StudyRoomFixture.createStudyRoomRequestFixture(), roomAdmin.getId());

        studyRoom = studyRoomRepository.findById(1L).get();
    }

    @Test
    @DisplayName("스터디 룸 생성에 성공한다.")
    void studyRoom_create_Success() {
        //given
        User user = UserFixture.createRoomAdmin();
        CreateStudyRoomRequest request =  StudyRoomFixture.createStudyRoomRequestFixture();

        //when
        commandService.create(request, user.getId());

        //then
        Optional<StudyRoom> studyRoom = studyRoomRepository.findById(2L);

        assertThat(studyRoom.isPresent()).isTrue();
    }

    @Test
    @DisplayName("스터디 룸 수정에 성공한다.")
    void studyRoom_update_Success() {
        //given
        UpdateStudyRoomRequest request = StudyRoomFixture.createUpdateStudyRoomRequest();

        //when
        commandService.update(request, studyRoom.getId(), roomAdmin.getId());

        //then
        Optional<StudyRoom> findStudyRoom = studyRoomRepository.findById(studyRoom.getId());
        assertThat(findStudyRoom.isPresent()).isTrue();
        assertThat(findStudyRoom.get().getTitle()).isEqualTo(request.getTitle());

        /**
         * .imageModification(ModifyStudyRoomImageRequest.builder()
         *      .imageIdsToRemove(List.of(1L, 2L))
         *      .imagesToAdd(List.of("http://test1.png", "http://test2.png"))
         *      .build()
         * )
         */
        assertThat(imageRepository.count()).isEqualTo(2); // 수정 - (2개 삭제하고 2개 추가)
    }

    @Test
    @DisplayName("스터디 룸의 어드민이 아닌 경우 수정에 실패한다.")
    void studyRoom_update_whenNotRoomAdmin_thenFail() {
        //given
        UpdateStudyRoomRequest request = StudyRoomFixture.createUpdateStudyRoomRequest();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, studyRoom.getId(), UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 잘못된 태그 ID값을 전달했을 경우 실패한다.")
    void studyRoom_update_whenNotValidTagId_thenFail() {
        //given
        UpdateStudyRoomRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForTagFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 태그 제한 개수를 초과했을 경우 실패한다.")
    void studyRoom_update_whenExceedTagLimit_thenFail() {
        //given
        UpdateStudyRoomRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForTagLimitFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 잘못된 이미지 ID값을 전달했을 경우 실패한다.")
    void studyRoom_update_whenNotValidImageId_thenFail() {
        //given
        UpdateStudyRoomRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForImageFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 이미지 제한 개수를 초과했을 경우 실패한다.")
    void studyRoom_update_whenExceedImageTagLimit_thenFail() {
        //given
        UpdateStudyRoomRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForImageLimitFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 잘못된 휴무일 ID값을 전달했을 경우 실패한다.")
    void studyRoom_update_whenNotValidDayOffId_thenFail() {
        //given
        UpdateStudyRoomRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForDayOffFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 휴무일 제한 개수를 초과했을 경우 실패한다.")
    void studyRoom_update_whenExceedDayOffLimit_thenFail() {
        //given
        UpdateStudyRoomRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForDayOffLimitFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 중복된 휴무일을 추가했을 경우 실패한다.")
    void studyRoom_update_whenDuplicatedDayOff_thenFail(){
        //given
        UpdateStudyRoomRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForDayOffDuplicatedFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.update(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 설정 수정에 성공한다.")
    void studyRoom_updateSettings_Success() {
        //given
        UpdateStudyRoomSettingRequest request = StudyRoomFixture.createUpdateStudyRoomSettingRequest();

        //when
        commandService.updateSettings(request, studyRoom.getId(), roomAdmin.getId());

        //then
        Optional<StudyRoom> findStudyRoom = studyRoomRepository.findById(studyRoom.getId());
        assertThat(findStudyRoom.isPresent()).isTrue();

        /**
         * .optionInfoModification(ModifyStudyRoomOptionInfoRequest.builder()
         *      .optionsIdsToRemove(List.of(1L, 2L))
         *      .optionsToAdd(List.of(StudyRoomOption.ELECTRICAL, StudyRoomOption.WIFI))
         *      .build()
         *)
         */
        assertThat(optionInfoRepository.count()).isEqualTo(2); // 수정 - (2개 삭제하고 2개 추가)

        Optional<StudyRoomReserveType> findStudyRoomReserveType
                = reserveTypeRepository.findById(1L);

        assertThat(findStudyRoomReserveType.isPresent()).isTrue();
        assertThat(findStudyRoomReserveType.get().getPricePerHour()).isEqualTo(10000);
        assertThat(findStudyRoomReserveType.get().getMaxHeadcount()).isEqualTo(99);
    }

    @Test
    @DisplayName("스터디 룸 수정시 잘못된 옵션 정보 ID값을 전달했을 경우 실패한다.")
    void studyRoom_updateSettings_whenNotValidOptionInfoId_thenFail() {
        //given
        UpdateStudyRoomSettingRequest request = StudyRoomFixture.createUpdateStudyRoomSettingRequestForOptionInfoFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 중복된 옵션을 추가했을 경우 실패한다.")
    void studyRoom_update_whenDuplicatedOption_thenFail(){
        //given
        UpdateStudyRoomSettingRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForOptionDuplicatedFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 잘못된 타입 정보 ID값을 전달했을 경우 실패한다.")
    void studyRoom_updateSettings_whenNotValidTypeInfoId_thenFail() {
        //given
        UpdateStudyRoomSettingRequest request = StudyRoomFixture.createUpdateStudyRoomSettingRequestForTypeInfoFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 스터디 룸 타입 제한 개수를 초과했을 경우 실패한다.")
    void studyRoom_update_whenExceedTypeLimit_thenFail() {
        //given
        UpdateStudyRoomSettingRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForTypeLimitFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 중복된 스터디 룸 타입을 추가했을 경우 실패한다.")
    void studyRoom_update_whenDuplicatedType_thenFail(){
        //given
        UpdateStudyRoomSettingRequest request = StudyRoomFixture.createUpdateStudyRoomRequestForTypeDuplicatedFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정시 잘못된 예약 타입 ID값을 전달했을 경우 실패한다.")
    void studyRoom_updateSettings_whenNotValidReserveTypeId_thenFail() {
        //given
        UpdateStudyRoomSettingRequest request
                = StudyRoomFixture.createUpdateStudyRoomSettingRequestForReserveTypeFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 삭제에 성공한다.")
    void studyRoom_delete_Success() {
        //when
        commandService.delete(studyRoom.getId(), roomAdmin.getId());

        //then
        Optional<StudyRoom> findStudyRoom = studyRoomRepository.findById(studyRoom.getId());

        assertThat(findStudyRoom.isPresent()).isFalse();
    }

    @Test
    @DisplayName("스터디 룸 좋아요 동시성 테스트에 성공한다.")
    void studyRoom_like_concurrency_test_Success() throws InterruptedException {
        //given
        List<UUID> userUuids = createTestUsers();
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
    void studyRoom_dislike_concurrency_test_Success() throws InterruptedException {
        //given
        List<UUID> userUuids = createTestUsers();
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

    @Test
    @DisplayName("이미 좋아요를 생성한 유저라면 스터디 룸 좋아요 생성에 실패한다.")
    void studyRoom_like_whenAlreadyLike_thenFail() {
        //given
        commandService.createLike(studyRoom.getId(), roomAdmin.getId());

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.createLike(studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 북마크 생성에 성공한다.")
    void studyRoom_createBookmark_Success() {
        //when
        commandService.createBookmark(studyRoom.getId(), roomAdmin.getId());

        //then
        boolean result =
                bookmarkRepository.existsByStudyRoomIdAndUserId(studyRoom.getId(), roomAdmin.getId());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이미 북마크를 생성한 유저라면 스터디 룸 북마크 생성에 실패한다.")
    void studyRoom_bookmark_whenAlreadyBookmark_thenFail() {
        //given
        commandService.createBookmark(studyRoom.getId(), roomAdmin.getId());

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.createBookmark(studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 북마크 삭제에 성공한다.")
    void studyRoom_deleteBookmark_Success() {
        //given
        commandService.createBookmark(studyRoom.getId(), roomAdmin.getId());

        //when
        commandService.unBookmark(1L, roomAdmin.getId());

        //then
        boolean result =
                bookmarkRepository.existsByStudyRoomIdAndUserId(studyRoom.getId(), roomAdmin.getId());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("관련 스터디 룸 북마크가 없는 경우 삭제에 실패한다.")
    void studyRoom_deleteBookmark_whenNotExistsBookmark_thenFail() {
        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.unBookmark(studyRoom.getId(), roomAdmin.getId())
        );
    }

    private List<UUID> createTestUsers() {
        List<User> users = UserFixture.multiUser(THREAD_COUNT);

        userRepository.saveAll(users);

        return users.stream().map(User::getId).toList();
    }
}

