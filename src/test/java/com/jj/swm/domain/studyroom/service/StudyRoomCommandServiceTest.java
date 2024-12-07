package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.update.image.StudyRoomImageModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.tag.StudyRoomTagModifyRequest;
import com.jj.swm.domain.studyroom.entity.StudyRoomImage;
import com.jj.swm.domain.studyroom.fixture.*;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomCreateRequest;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.fixture.dto.StudyRoomCreateRequestFixture;
import com.jj.swm.domain.studyroom.fixture.dto.update.StudyRoomTagModifyRequestFixture;
import com.jj.swm.domain.studyroom.fixture.dto.update.StudyRoomUpdateRequestFixture;
import com.jj.swm.domain.studyroom.repository.*;
import com.jj.swm.domain.user.UserFixture;
import com.jj.swm.domain.user.entity.RoleType;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyRoomCommandServiceTest {

    @Mock private StudyRoomRepository studyRoomRepository;
    @Mock private UserRepository userRepository;
    @Mock private StudyRoomDayOffRepository dayOffRepository;
    @Mock private StudyRoomImageRepository imageRepository;
    @Mock private StudyRoomOptionInfoRepository optionInfoRepository;
    @Mock private StudyRoomTypeInfoRepository typeInfoRepository;
    @Mock private StudyRoomReserveTypeRepository reserveTypeRepository;
    @Mock private StudyRoomTagRepository tagRepository;

    @InjectMocks private StudyRoomCommandService studyRoomCommandService;

    private StudyRoom studyRoom;
    private User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.createUser();
        studyRoom = StudyRoomFixture.create(user);
    }

    @Test
    @DisplayName("스터디 룸을 생성할 수 있다.")
    void createStudyRoom_Success() throws Exception{
        //given
        StudyRoomCreateRequest request = StudyRoomCreateRequestFixture.create();

        given(userRepository.findByIdAndUserRole(UserFixture.uuid, RoleType.ROOM_ADMIN))
                .willReturn(Optional.ofNullable(user));

        given(studyRoomRepository.save(any(StudyRoom.class))).willReturn(studyRoom);

        //when
        studyRoomCommandService.create(request, UserFixture.uuid);

        //then
        verify(studyRoomRepository, times(1)).save(any(StudyRoom.class));
        verify(dayOffRepository, times(1)).batchInsert(request.getDayOffs(), studyRoom);
        verify(tagRepository, times(1)).batchInsert(request.getTags(), studyRoom);
        verify(imageRepository, times(1)).batchInsert(request.getImageUrls(), studyRoom);
        verify(optionInfoRepository, times(1)).batchInsert(request.getOptions(), studyRoom);
        verify(typeInfoRepository, times(1)).batchInsert(request.getTypes(), studyRoom);
        verify(reserveTypeRepository, times(1)).batchInsert(request.getReservationTypes(), studyRoom);
    }

    @Test
    @DisplayName("존재하지 않는 UUID라면 예외를 반환한다.")
    void createStudyRoom_FailByUserUUIDNotFound() throws Exception{
        //given
        StudyRoomCreateRequest request = StudyRoomCreateRequestFixture.create();

        UUID uuid = UUID.randomUUID();

        given(userRepository.findByIdAndUserRole(uuid, RoleType.ROOM_ADMIN))
                .willReturn(Optional.empty());

        //when & then
        assertThrows(GlobalException.class, () -> studyRoomCommandService.create(request, uuid));
    }

    @Test
    @DisplayName("스터디 룸 생성 시 모든 List가 empty인 경우를 수행한다.")
    void createStudyRoom_WhenConditionListEmpty_Success() throws Exception{
        //given
        StudyRoomCreateRequest request = StudyRoomCreateRequestFixture.createListEmpty();

        given(userRepository.findByIdAndUserRole(UserFixture.uuid, RoleType.ROOM_ADMIN))
                .willReturn(Optional.ofNullable(user));

        given(studyRoomRepository.save(any(StudyRoom.class))).willReturn(studyRoom);

        //when
        studyRoomCommandService.create(request, UserFixture.uuid);

        //then
        verify(studyRoomRepository, times(1)).save(any(StudyRoom.class));
        verify(dayOffRepository, never()).batchInsert(request.getDayOffs(), studyRoom);
        verify(tagRepository, never()).batchInsert(request.getTags(), studyRoom);
        verify(imageRepository, times(1)).batchInsert(request.getImageUrls(), studyRoom);
        verify(optionInfoRepository, times(1)).batchInsert(request.getOptions(), studyRoom);
        verify(typeInfoRepository, times(1)).batchInsert(request.getTypes(), studyRoom);
        verify(reserveTypeRepository, times(1)).batchInsert(request.getReservationTypes(), studyRoom);
    }

    @Test
    @DisplayName("스터디 룸 생성 시 모든 List가 null인 경우를 수행한다.")
    void createStudyRoom_WhenConditionListNull_Success() throws Exception{
        //given
        StudyRoomCreateRequest request = StudyRoomCreateRequestFixture.createListNull();

        given(userRepository.findByIdAndUserRole(UserFixture.uuid, RoleType.ROOM_ADMIN))
                .willReturn(Optional.ofNullable(user));

        given(studyRoomRepository.save(any(StudyRoom.class))).willReturn(studyRoom);

        //when
        studyRoomCommandService.create(request, UserFixture.uuid);

        //then
        verify(studyRoomRepository, times(1)).save(any(StudyRoom.class));
        verify(dayOffRepository, never()).batchInsert(request.getDayOffs(), studyRoom);
        verify(tagRepository, never()).batchInsert(request.getTags(), studyRoom);
        verify(imageRepository, times(1)).batchInsert(request.getImageUrls(), studyRoom);
        verify(optionInfoRepository, times(1)).batchInsert(request.getOptions(), studyRoom);
        verify(typeInfoRepository, times(1)).batchInsert(request.getTypes(), studyRoom);
        verify(reserveTypeRepository, times(1)).batchInsert(request.getReservationTypes(), studyRoom);
    }

    @Test
    @DisplayName("스터디 룸을 수정할 수 있다.")
    void updateStudyRoom_Success() throws Exception{
        //given
        given(studyRoomRepository.findByIdAndUserId(1L, UserFixture.uuid))
                .willReturn(Optional.ofNullable(studyRoom));

        StudyRoomUpdateRequest updateRequest = StudyRoomUpdateRequestFixture.create();
        StudyRoomImageModifyRequest imageModifyRequest = updateRequest.getImageModification();

        StudyRoomImage imageId1 = StudyRoomImageFixture.createWithId(studyRoom, 1L);

        doNothing().when(imageRepository).batchInsert(
                imageModifyRequest.getImagesToAdd(), studyRoom
        );

        given(imageRepository.findAllByIdInAndStudyRoom(List.of(1L), studyRoom)).willReturn(List.of(imageId1));
        given(imageRepository.countStudyRoomImageByIdInAndStudyRoom(List.of(2L, 3L), studyRoom)).willReturn(2);
        doNothing().when(imageRepository).deleteAllByIdInBatch(List.of(2L, 3L));

        //when
        studyRoomCommandService.update(updateRequest, UserFixture.uuid);

        //then
        assertThat(studyRoom.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(studyRoom.getSubtitle()).isEqualTo(updateRequest.getSubtitle());
        assertThat(imageId1.getImageUrl()).isEqualTo(imageModifyRequest.getImagesToUpdate().getFirst().getImageUrl());
        verify(studyRoomRepository, times(1)).findByIdAndUserId(1L, UserFixture.uuid);
        verify(imageRepository, times(1)).findAllByIdInAndStudyRoom(List.of(1L), studyRoom);
        verify(imageRepository, times(1)).countStudyRoomImageByIdInAndStudyRoom(List.of(2L, 3L), studyRoom);
        verify(imageRepository, times(1)).deleteAllByIdInBatch(List.of(2L, 3L));
    }

    @Test
    @DisplayName("스터디 룸 이미지 수정값이 올바르지 않다면 오류를 반환한다.")
    void updateStudyRoom_FailByNotValidImageUpdate() throws Exception{
        //given
        given(studyRoomRepository.findByIdAndUserId(1L, UserFixture.uuid))
                .willReturn(Optional.ofNullable(studyRoom));

        StudyRoomUpdateRequest updateRequest = StudyRoomUpdateRequestFixture.createOnlyImageUpdate();

        given(imageRepository.findAllByIdInAndStudyRoom(List.of(1L), studyRoom)).willReturn(List.of());

        //when & then
        assertThrows(GlobalException.class, () -> studyRoomCommandService.update(updateRequest, UserFixture.uuid));
    }

    @Test
    @DisplayName("스터디 룸 이미지 삭제값이 올바르지 않다면 오류를 반환한다.")
    void updateStudyRoom_FailByNotValidImageRemove() throws Exception{
        //given
        given(studyRoomRepository.findByIdAndUserId(1L, UserFixture.uuid))
                .willReturn(Optional.ofNullable(studyRoom));

        StudyRoomUpdateRequest updateRequest = StudyRoomUpdateRequestFixture.createOnlyImageRemove();

        given(imageRepository.countStudyRoomImageByIdInAndStudyRoom(List.of(2L, 3L), studyRoom)).willReturn(1);

        //when & then
        assertThrows(GlobalException.class, () -> studyRoomCommandService.update(updateRequest, UserFixture.uuid));
    }

    @Test
    @DisplayName("스터디 룸 태그를 수정할 수 있다.")
    void updateStudyRoomTag_Success() throws Exception{
        //given
        given(studyRoomRepository.findByIdAndUserId(1L, UserFixture.uuid))
                .willReturn(Optional.ofNullable(studyRoom));

        StudyRoomTagModifyRequest tagModifyRequest = StudyRoomTagModifyRequestFixture.create();


        doNothing().when(tagRepository).batchInsert(tagModifyRequest.getTagsToAdd(), studyRoom);

        given(tagRepository.findAllByIdInAndStudyRoom(List.of(1L), studyRoom)).willReturn(List.of());
        given(imageRepository.countStudyRoomImageByIdInAndStudyRoom(List.of(2L, 3L), studyRoom)).willReturn(2);
        //when

        //then
    }

}
