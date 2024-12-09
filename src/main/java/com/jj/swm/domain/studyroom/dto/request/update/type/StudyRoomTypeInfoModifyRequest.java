package com.jj.swm.domain.studyroom.dto.request.update.type;

import com.jj.swm.domain.studyroom.entity.StudyRoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomTypeInfoModifyRequest {

    private List<StudyRoomType> typesToAdd;
    private List<StudyRoomTypeUpdateRequest> typesToUpdate;
    private List<Long> typeIdsToRemove;
}
