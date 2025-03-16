package com.jj.swm.domain.studyroom.core.fixture.dto.request;

import com.jj.swm.domain.studyroom.core.dto.request.CreateStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.core.dto.request.UpdateStudyRoomAssociationsRequest;
import com.jj.swm.domain.studyroom.core.dto.request.update.ModifyStudyRoomOptionInfoRequest;
import com.jj.swm.domain.studyroom.core.dto.request.update.ModifyStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.core.dto.request.update.ModifyStudyRoomTypeInfoRequest;
import com.jj.swm.domain.studyroom.core.dto.request.update.UpdateStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomOption;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomType;

import java.util.Arrays;
import java.util.List;

public class UpdateStudyRoomAssociationsRequestFixture {

    public static UpdateStudyRoomAssociationsRequest create(){
        return UpdateStudyRoomAssociationsRequest.builder()
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

    public static UpdateStudyRoomAssociationsRequest createForOptionInfoFail() {
        return UpdateStudyRoomAssociationsRequest.builder()
                .optionInfoModification(ModifyStudyRoomOptionInfoRequest.builder()
                        .optionsIdsToRemove(List.of(100L, 200L))
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomAssociationsRequest createForTypeInfoFail() {
        return UpdateStudyRoomAssociationsRequest.builder()
                .typeInfoModification(ModifyStudyRoomTypeInfoRequest.builder()
                        .typeIdsToRemove(List.of(100L, 200L))
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomAssociationsRequest createForReserveTypeFail() {
        return UpdateStudyRoomAssociationsRequest.builder()
                .reservationTypeModification(ModifyStudyRoomReservationTypeRequest.builder()
                        .reservationTypeIdsToRemove(List.of(100L))
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomAssociationsRequest createForTypeLimitFail() {
        return UpdateStudyRoomAssociationsRequest.builder()
                .typeInfoModification(ModifyStudyRoomTypeInfoRequest.builder()
                        .typesToAdd(Arrays.stream(StudyRoomType.values()).toList())
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomAssociationsRequest createForTypeDuplicatedFail() {
        return UpdateStudyRoomAssociationsRequest.builder()
                .typeInfoModification(ModifyStudyRoomTypeInfoRequest.builder()
                        .typesToAdd(List.of(StudyRoomType.STUDY))
                        .build()
                )
                .build();
    }

    public static UpdateStudyRoomAssociationsRequest createForOptionDuplicatedFail() {
        return UpdateStudyRoomAssociationsRequest.builder()
                .optionInfoModification(ModifyStudyRoomOptionInfoRequest.builder()
                        .optionsToAdd(List.of(StudyRoomOption.MIKE))
                        .build()
                )
                .build();
    }
}
