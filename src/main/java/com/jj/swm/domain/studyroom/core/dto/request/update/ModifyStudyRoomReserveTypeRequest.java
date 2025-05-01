package com.jj.swm.domain.studyroom.core.dto.request.update;

import com.jj.swm.domain.studyroom.core.dto.request.CreateStudyRoomReserveTypeRequest;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomReserveTypeRequest {

    private List<CreateStudyRoomReserveTypeRequest> reserveTypesToAdd;

    private List<UpdateStudyRoomReserveTypeRequest> reserveTypesToUpdate;

    private List<Long> reserveTypeIdsToRemove;
}
