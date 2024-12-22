package com.jj.swm.domain.studyroom.dto.response;

import com.jj.swm.domain.studyroom.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.entity.StudyRoomTypeInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStudyRoomTypeInfoResponse {

    private Long typeInfoId;
    private StudyRoomType type;

    public static GetStudyRoomTypeInfoResponse from(StudyRoomTypeInfo typeInfo) {
        return GetStudyRoomTypeInfoResponse.builder()
                .typeInfoId(typeInfo.getId())
                .type(typeInfo.getType())
                .build();
    }
}
