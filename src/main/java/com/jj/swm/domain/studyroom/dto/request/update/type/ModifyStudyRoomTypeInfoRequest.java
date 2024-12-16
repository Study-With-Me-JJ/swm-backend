package com.jj.swm.domain.studyroom.dto.request.update.type;

import com.jj.swm.domain.studyroom.entity.StudyRoomType;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomTypeInfoRequest {

    private List<StudyRoomType> typesToAdd;
    private List<UpdateStudyRoomTypeRequest> typesToUpdate;
    private List<Long> typeIdsToRemove;
}
