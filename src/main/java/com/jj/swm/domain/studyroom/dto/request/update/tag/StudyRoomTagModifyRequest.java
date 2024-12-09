package com.jj.swm.domain.studyroom.dto.request.update.tag;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomTagModifyRequest {

    private List<String> tagsToAdd;
    private List<StudyRoomTagUpdateRequest> tagsToUpdate;
    private List<Long> tagIdsToRemove;
}
