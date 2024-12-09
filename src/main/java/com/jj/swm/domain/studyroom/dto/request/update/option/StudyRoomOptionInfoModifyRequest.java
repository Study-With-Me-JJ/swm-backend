package com.jj.swm.domain.studyroom.dto.request.update.option;

import com.jj.swm.domain.studyroom.entity.StudyRoomOption;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomOptionInfoModifyRequest {

    private List<StudyRoomOption> optionsToAdd;
    private List<StudyRoomOptionUpdateRequest> optionsToUpdate;
    private List<Long> optionsIdsToRemove;
}
