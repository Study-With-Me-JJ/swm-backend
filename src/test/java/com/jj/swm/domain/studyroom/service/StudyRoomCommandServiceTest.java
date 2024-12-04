package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.StudyRoomFixture;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomCreateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomReservationTypeCreateRequest;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.entity.embeddable.Point;
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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        studyRoom = StudyRoomFixture.createStudyRoom(user);
    }

    @Test
    @DisplayName("스터디 룸을 생성할 수 있다.")
    void createStudyRoom_Success() throws Exception{
        //given
        StudyRoomCreateRequest request = createStudyRoomCreateRequestFixture();

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
        StudyRoomCreateRequest request = createStudyRoomCreateRequestFixture();

        UUID uuid = UUID.randomUUID();

        given(userRepository.findByIdAndUserRole(uuid, RoleType.ROOM_ADMIN))
                .willReturn(Optional.empty());

        //when & then
        assertThrows(GlobalException.class, () -> studyRoomCommandService.create(request, uuid));
    }


    StudyRoomCreateRequest createStudyRoomCreateRequestFixture(){
        return StudyRoomCreateRequest.builder()
                .title("test")
                .subtitle("test")
                .introduce("test")
                .notice("test")
                .guideline("test")
                .openingTime(LocalTime.MIN)
                .closingTime(LocalTime.MAX)
                .address(Address.builder()
                        .address("서울 동작구")
                        .detailAddress("서울 동작구 23번길")
                        .region("서울")
                        .locality("동작구")
                        .build())
                .point(Point.builder()
                        .latitude(0.0)
                        .longitude(0.0)
                        .build())
                .thumbnail("http://test.png")
                .referenceUrl("http://test.com")
                .phoneNumber("010-0000-0000")
                .tags(List.of("태그1", "태그2", "태그3"))
                .dayOffs(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY))
                .imageUrls(List.of("http://test1.png", "http://test2.png"))
                .types(List.of(StudyRoomType.STUDY, StudyRoomType.MEETING))
                .options(List.of(StudyRoomOption.MIKE, StudyRoomOption.NO_SMOKE))
                .reservationTypes(
                        List.of(createStudyRoomReservationTypeCreateRequestFixture())
                )
                .minReserveTime(2)
                .entireMinHeadcount(1)
                .entireMaxHeadcount(2)
                .build();
    }

    StudyRoomReservationTypeCreateRequest createStudyRoomReservationTypeCreateRequestFixture(){
        return StudyRoomReservationTypeCreateRequest.builder()
                .maxHeadcount(1)
                .reservationOption("1인실")
                .pricePerHour(1000)
                .build();
    }
}
