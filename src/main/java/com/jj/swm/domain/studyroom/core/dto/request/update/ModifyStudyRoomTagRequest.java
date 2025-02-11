package com.jj.swm.domain.studyroom.core.dto.request.update;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomTagRequest {

    private List<String> tagsToAdd;

    private List<Long> tagIdsToRemove;
}
