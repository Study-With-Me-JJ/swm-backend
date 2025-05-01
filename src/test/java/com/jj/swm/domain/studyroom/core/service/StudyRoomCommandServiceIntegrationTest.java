package com.jj.swm.domain.studyroom.core.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.core.fixture.dto.request.CreateStudyRoomRequestFixture;
import com.jj.swm.domain.studyroom.core.fixture.dto.request.UpdateStudyRoomAssociationsRequestFixture;
import com.jj.swm.domain.studyroom.core.fixture.dto.request.UpdateStudyRoomSettingsRequestFixture;
import com.jj.swm.domain.studyroom.core.repository.*;
import com.jj.swm.domain.studyroom.core.dto.request.CreateStudyRoomRequest;
import com.jj.swm.domain.studyroom.core.dto.request.UpdateStudyRoomSettingsRequest;
import com.jj.swm.domain.studyroom.core.dto.request.UpdateStudyRoomAssociationsRequest;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomReserveType;
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

import static com.jj.swm.domain.user.helper.UserTestHelper.insertUsersAndGetUserIds;
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
        commandService.createStudyRoom(CreateStudyRoomRequestFixture.create(), roomAdmin.getId());

        studyRoom = studyRoomRepository.findById(1L).get();
    }

    @Test
    @DisplayName("스터디 룸 생성에 성공한다.")
    void createStudyRoom_Success() {
        //given
        User user = UserFixture.createRoomAdmin();
        CreateStudyRoomRequest request = CreateStudyRoomRequestFixture.create();

        //when
        commandService.createStudyRoom(request, user.getId());

        //then
        Optional<StudyRoom> studyRoom = studyRoomRepository.findById(2L);

        assertThat(studyRoom.isPresent()).isTrue();
    }

    @Test
    @DisplayName("스터디 룸 수정에 성공한다.")
    void updateStudyRoomSettings_Success() {
        //given
        UpdateStudyRoomSettingsRequest request = UpdateStudyRoomSettingsRequestFixture.create();

        //when
        commandService.updateStudyRoomSettings(request, studyRoom.getId(), roomAdmin.getId());

        //then
        Optional<StudyRoom> findStudyRoom = studyRoomRepository.findById(studyRoom.getId());
        assertThat(findStudyRoom.isPresent()).isTrue();
        assertThat(findStudyRoom.get().getTitle()).isEqualTo(request.getTitle());

        /**
         * .imageModification(ModifyStudyRoomImageRequest.builder()
         *      .imageIdsToUpdate(List.of("http://test1.png", "http://test2.png"))
         *      .build()
         * )
         */
        assertThat(imageRepository.count()).isEqualTo(2); // 수정 - (기존 데이터를 삭제하고 2개 추가)
    }

    @Test
    @DisplayName("스터디 룸의 어드민이 아닌 경우 수정에 실패한다.")
    void updateStudyRoomSettings_WhenNotRoomAdmin_ThenFail() {
        //given
        UpdateStudyRoomSettingsRequest request = UpdateStudyRoomSettingsRequestFixture.create();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomSettings(request, studyRoom.getId(), UUID.randomUUID())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 이미지 제한 개수를 초과했을 경우 실패한다.")
    void updateStudyRoomSettings_WhenExceedImageTagLimit_ThenFail() {
        //given
        UpdateStudyRoomSettingsRequest request = UpdateStudyRoomSettingsRequestFixture.createForImageLimitFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 잘못된 태그 ID값을 전달했을 경우 실패한다.")
    void updateStudyRoomSettings_WhenNotValidTagId_ThenFail() {
        //given
        UpdateStudyRoomSettingsRequest request = UpdateStudyRoomSettingsRequestFixture.createForTagFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 태그 제한 개수를 초과했을 경우 실패한다.")
    void updateStudyRoomSettings_WhenExceedTagLimit_ThenFail() {
        //given
        UpdateStudyRoomSettingsRequest request = UpdateStudyRoomSettingsRequestFixture.createForTagLimitFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 잘못된 휴무일 ID값을 전달했을 경우 실패한다.")
    void updateStudyRoomSettings_WhenNotValidDayOffId_ThenFail() {
        //given
        UpdateStudyRoomSettingsRequest request = UpdateStudyRoomSettingsRequestFixture.createForDayOffFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 휴무일 제한 개수를 초과했을 경우 실패한다.")
    void updateStudyRoomSettings_WhenExceedDayOffLimit_ThenFail() {
        //given
        UpdateStudyRoomSettingsRequest request = UpdateStudyRoomSettingsRequestFixture.createForDayOffLimitFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 중복된 휴무일을 추가했을 경우 실패한다.")
    void updateStudyRoomSettings_WhenDuplicatedDayOff_ThenFail(){
        //given
        UpdateStudyRoomSettingsRequest request = UpdateStudyRoomSettingsRequestFixture.createForDayOffDuplicatedFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomSettings(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 설정 수정에 성공한다.")
    void updateStudyRoomAssociations_Success() {
        //given
        UpdateStudyRoomAssociationsRequest request = UpdateStudyRoomAssociationsRequestFixture.create();

        //when
        commandService.updateStudyRoomAssociations(request, studyRoom.getId(), roomAdmin.getId());

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
    @DisplayName("스터디 룸 수정 시 잘못된 옵션 정보 ID값을 전달했을 경우 실패한다.")
    void updateStudyRoomAssociations_WhenNotValidOptionInfoId_ThenFail() {
        //given
        UpdateStudyRoomAssociationsRequest request = UpdateStudyRoomAssociationsRequestFixture.createForOptionInfoFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomAssociations(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 중복된 옵션을 추가했을 경우 실패한다.")
    void updateStudyRoomAssociations_WhenDuplicatedOption_ThenFail(){
        //given
        UpdateStudyRoomAssociationsRequest request
                = UpdateStudyRoomAssociationsRequestFixture.createForOptionDuplicatedFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomAssociations(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 잘못된 타입 정보 ID값을 전달했을 경우 실패한다.")
    void updateStudyRoomAssociations_WhenNotValidTypeInfoId_ThenFail() {
        //given
        UpdateStudyRoomAssociationsRequest request = UpdateStudyRoomAssociationsRequestFixture.createForTypeInfoFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomAssociations(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 스터디 룸 타입 제한 개수를 초과했을 경우 실패한다.")
    void updateStudyRoomAssociations_WhenExceedTypeLimit_ThenFail() {
        //given
        UpdateStudyRoomAssociationsRequest request = UpdateStudyRoomAssociationsRequestFixture.createForTypeLimitFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomAssociations(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 중복된 스터디 룸 타입을 추가했을 경우 실패한다.")
    void updateStudyRoomAssociations_WhenDuplicatedType_ThenFail(){
        //given
        UpdateStudyRoomAssociationsRequest request
                = UpdateStudyRoomAssociationsRequestFixture.createForTypeDuplicatedFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomAssociations(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 수정 시 잘못된 예약 타입 ID값을 전달했을 경우 실패한다.")
    void updateStudyRoomAssociations_WhenNotValidReserveTypeId_ThenFail() {
        //given
        UpdateStudyRoomAssociationsRequest request
                = UpdateStudyRoomAssociationsRequestFixture.createForReserveTypeFail();

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.updateStudyRoomAssociations(request, studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 삭제에 성공한다.")
    void deleteStudyRoomAndAssociations_AndAssociations_Success() {
        //when
        commandService.deleteStudyRoomAndAssociations(studyRoom.getId(), roomAdmin.getId());

        //then
        Optional<StudyRoom> findStudyRoom = studyRoomRepository.findById(studyRoom.getId());
        assertThat(findStudyRoom.isPresent()).isFalse();
    }

    @Test
    @DisplayName("스터디 룸 삭제 시 관련 스터디 룸이 존재하지 않는다면 삭제에 실패한다.")
    void deleteStudyRoomAndAssociations_WhenNotRelatedStudyRoom_AndAssociations_ThenFail() {
        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.deleteStudyRoomAndAssociations(100L, roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 다중 삭제에 성공한다.")
    void deleteStudyRoomsAndAssociations_AndAssociations_SuccessAndAssociations() {
        //when
        commandService.deleteStudyRoomsAndAssociations(List.of(studyRoom.getId()), roomAdmin.getId());

        //then
        Optional<StudyRoom> findStudyRoom = studyRoomRepository.findById(studyRoom.getId());
        assertThat(findStudyRoom.isPresent()).isFalse();
    }

    @Test
    @DisplayName("스터디 룸 다중 삭제 시 관련 스터디 룸이 존재하지 않는다면 삭제에 실패한다.")
    void deleteStudyRoomsAndAssociations_WhenNotRelatedSomeStudyRoom_AndAssociations_ThenFailAndAssociations() {
        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.deleteStudyRoomsAndAssociations(List.of(100L), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 좋아요 동시성 테스트에 성공한다.")
    void createStudyRoomLike_ConcurrencyTest_Success() throws InterruptedException {
        //given
        List<UUID> userUuids = insertUsersAndGetUserIds(userRepository, THREAD_COUNT);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        // when
        for(int i = 0; i < THREAD_COUNT; i++) {
            final UUID uuid = userUuids.get(i);
            executorService.submit(() -> {
                try {
                    commandService.createStudyRoomLike(studyRoom.getId(), uuid);
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
    void deleteStudyRoomLike_ConcurrencyTest_Success() throws InterruptedException {
        //given
        List<UUID> userUuids = insertUsersAndGetUserIds(userRepository, THREAD_COUNT);
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        countDownLatch = new CountDownLatch(THREAD_COUNT);

        for(UUID uuid : userUuids) {
           commandService.createStudyRoomLike(studyRoom.getId(), uuid);
        }

        //when
        for(int i = 0; i < THREAD_COUNT; i++) {
            final UUID uuid = userUuids.get(i);
            executorService.submit(() -> {
                try {
                    commandService.deleteStudyRoomLike(studyRoom.getId(), uuid);
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
    void createStudyRoomLike_WhenAlreadyLike_ThenFail() {
        //given
        commandService.createStudyRoomLike(studyRoom.getId(), roomAdmin.getId());

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.createStudyRoomLike(studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 북마크 생성에 성공한다.")
    void createStudyRoomBookmark_Success() {
        //when
        commandService.createStudyRoomBookmark(studyRoom.getId(), roomAdmin.getId());

        //then
        boolean result =
                bookmarkRepository.existsByStudyRoomIdAndUserId(studyRoom.getId(), roomAdmin.getId());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이미 북마크를 생성한 유저라면 스터디 룸 북마크 생성에 실패한다.")
    void createStudyRoomBookmark_whenAlreadyBookmark_ThenFail() {
        //given
        commandService.createStudyRoomBookmark(studyRoom.getId(), roomAdmin.getId());

        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.createStudyRoomBookmark(studyRoom.getId(), roomAdmin.getId())
        );
    }

    @Test
    @DisplayName("스터디 룸 북마크 삭제에 성공한다.")
    void deleteStudyRoomBookmark_Success() {
        //given
        commandService.createStudyRoomBookmark(studyRoom.getId(), roomAdmin.getId());

        //when
        commandService.deleteStudyRoomBookmark(1L, roomAdmin.getId());

        //then
        boolean result =
                bookmarkRepository.existsByStudyRoomIdAndUserId(studyRoom.getId(), roomAdmin.getId());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("관련 스터디 룸 북마크가 없는 경우 삭제에 실패한다.")
    void deleteStudyRoomBookmark_WhenNotExistsBookmark_ThenFail() {
        //when & then
        Assertions.assertThrows(GlobalException.class,
                () -> commandService.deleteStudyRoomBookmark(studyRoom.getId(), roomAdmin.getId())
        );
    }
}

