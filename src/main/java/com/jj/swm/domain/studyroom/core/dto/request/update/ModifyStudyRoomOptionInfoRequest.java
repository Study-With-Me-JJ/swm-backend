package com.jj.swm.domain.studyroom.core.dto.request.update;

import com.jj.swm.domain.studyroom.core.entity.StudyRoomOption;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomOptionInfoRequest {

    private List<StudyRoomOption> optionsToAdd;

    private List<Long> optionsIdsToRemove;
}
