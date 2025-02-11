package com.jj.swm.domain.studyroom.core.dto.response;

import com.jj.swm.domain.studyroom.core.entity.StudyRoomType;
import com.jj.swm.domain.studyroom.core.entity.StudyRoomTypeInfo;
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
