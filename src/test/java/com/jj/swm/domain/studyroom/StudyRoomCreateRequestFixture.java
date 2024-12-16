package com.jj.swm.domain.studyroom;

import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomRequest;
import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.entity.embeddable.Address;
import com.jj.swm.domain.studyroom.entity.embeddable.Point;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class StudyRoomCreateRequestFixture {

    public static CreateStudyRoomRequest createStudyRoomCreateRequestFixture(){
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
                .build();
    }

    public static CreateStudyRoomRequest createStudyRoomCreateRequestListNullFixture(){
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
                .tags(null)
                .dayOffs(null)
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

    public static CreateStudyRoomRequest createStudyRoomCreateRequestListEmptyFixture(){
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
                .tags(List.of())
                .dayOffs(List.of())
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

    public static CreateStudyRoomReservationTypeRequest createStudyRoomReservationTypeCreateRequestFixture(){
        return CreateStudyRoomReservationTypeRequest.builder()
                .maxHeadcount(1)
                .reservationOption("1인실")
                .pricePerHour(1000)
                .build();
    }
}
