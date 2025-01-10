package com.jj.swm.domain.studyroom.fixture;

import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomRequest;
import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomSettingRequest;
import com.jj.swm.domain.studyroom.dto.request.update.*;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.entity.embeddable.Point;
import com.jj.swm.domain.user.entity.User;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StudyRoomFixture {

    public static AtomicInteger count = new AtomicInteger(1);

    public static StudyRoom createStudyRoom(User user) {
        return StudyRoom.builder()
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
                .minReserveTime(2)
                .entireMinHeadcount(1)
                .entireMaxHeadcount(2)
                .entireMinPricePerHour(count.getAndIncrement() * 1000)
                .entireMaxPricePerHour(10000)
                .user(user)
                .deletedAt(null)
                .build();
    }

    public static CreateStudyRoomRequest createStudyRoomRequestFixture(){
        return CreateStudyRoomRequest.builder()
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
                .entireMinPricePerHour(1000)
                .entireMaxPricePerHour(10000)
                .build();
    }

    public static CreateStudyRoomReservationTypeRequest createStudyRoomReservationTypeCreateRequestFixture(){
        return CreateStudyRoomReservationTypeRequest.builder()
                .maxHeadcount(1)
                .reservationOption("1인실")
                .pricePerHour(1000)
                .build();
    }

    public static UpdateStudyRoomRequest createUpdateStudyRoomRequest(){
        return UpdateStudyRoomRequest.builder()
                .title("update_test")
                .introduce("update_test")
                .notice("update_test")
                .guideline("update_test")
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
                .minReserveTime(2)
                .entireMinHeadcount(1)
                .entireMaxHeadcount(2)
                .imageModification(ModifyStudyRoomImageRequest.builder()
                        .imageIdsToRemove(List.of(1L, 2L))
                        .imagesToAdd(List.of("http://test1.png", "http://test2.png"))
                        .build()
                )
                .dayOffModification(ModifyStudyRoomDayOffRequest.builder()
                        .dayOffIdsToRemove(List.of(1L, 2L))
                        .dayOffsToAdd(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
                        .build())
                .tagModification(ModifyStudyRoomTagRequest.builder()
                        .tagIdsToRemove(List.of(1L, 2L))
                        .tagsToAdd(List.of("tag1", "tag2"))
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomRequest createUpdateStudyRoomRequestForImageFail() {
        return UpdateStudyRoomRequest.builder()
                .title("update_test")
                .introduce("update_test")
                .notice("update_test")
                .guideline("update_test")
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
                .minReserveTime(2)
                .entireMinHeadcount(1)
                .entireMaxHeadcount(2)
                .imageModification(ModifyStudyRoomImageRequest.builder()
                        .imageIdsToRemove(List.of(100L, 200L))
                        .build()
                )
                .dayOffModification(null)
                .tagModification(null)
                .build();
    }

    public static UpdateStudyRoomRequest createUpdateStudyRoomRequestForTagFail(){
        return UpdateStudyRoomRequest.builder()
                .title("update_test")
                .introduce("update_test")
                .notice("update_test")
                .guideline("update_test")
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
                .minReserveTime(2)
                .entireMinHeadcount(1)
                .entireMaxHeadcount(2)
                .imageModification(null)
                .dayOffModification(null)
                .tagModification(ModifyStudyRoomTagRequest.builder()
                        .tagIdsToRemove(List.of(100L, 200L))
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomRequest createUpdateStudyRoomRequestForDayOffFail() {
        return UpdateStudyRoomRequest.builder()
                .title("update_test")
                .introduce("update_test")
                .notice("update_test")
                .guideline("update_test")
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
                .minReserveTime(2)
                .entireMinHeadcount(1)
                .entireMaxHeadcount(2)
                .imageModification(null)
                .dayOffModification(ModifyStudyRoomDayOffRequest.builder()
                        .dayOffIdsToRemove(List.of(100L, 200L))
                        .build()
                )
                .tagModification(null)
                .build();
    }

    public static UpdateStudyRoomSettingRequest createUpdateStudyRoomSettingRequest(){
        return UpdateStudyRoomSettingRequest.builder()
                .optionInfoModification(ModifyStudyRoomOptionInfoRequest.builder()
                        .optionsIdsToRemove(List.of(1L, 2L))
                        .optionsToAdd(List.of(StudyRoomOption.ELECTRICAL, StudyRoomOption.WIFI))
                        .build()
                )
                .typeInfoModification(ModifyStudyRoomTypeInfoRequest.builder()
                        .typeIdsToRemove(List.of(1L, 2L))
                        .typesToAdd(List.of(StudyRoomType.PARTY))
                        .build()
                )
                .reservationTypeModification(ModifyStudyRoomReservationTypeRequest.builder()
                        .reservationTypesToUpdate(List.of(
                            UpdateStudyRoomReservationTypeRequest.builder()
                                    .reservationTypeId(1L)
                                    .reservationType(CreateStudyRoomReservationTypeRequest.builder()
                                            .reservationOption("99인실")
                                            .pricePerHour(10000)
                                            .maxHeadcount(99)
                                            .build()
                                    )
                                    .build()
                        ))
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomSettingRequest createUpdateStudyRoomSettingRequestForOptionInfoFail() {
        return UpdateStudyRoomSettingRequest.builder()
                .optionInfoModification(ModifyStudyRoomOptionInfoRequest.builder()
                        .optionsIdsToRemove(List.of(100L, 200L))
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomSettingRequest createUpdateStudyRoomSettingRequestForTypeInfoFail() {
        return UpdateStudyRoomSettingRequest.builder()
                .typeInfoModification(ModifyStudyRoomTypeInfoRequest.builder()
                        .typeIdsToRemove(List.of(100L, 200L))
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomSettingRequest createUpdateStudyRoomSettingRequestForReserveTypeFail() {
        return UpdateStudyRoomSettingRequest.builder()
                .reservationTypeModification(ModifyStudyRoomReservationTypeRequest.builder()
                        .reservationTypeIdsToRemove(List.of(100L))
                        .build()
                )
                .build();
    }
}
