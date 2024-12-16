package com.jj.swm.domain.studyroom.dto.request.update.tag;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomTagRequest {

    private List<String> tagsToAdd;
    private List<UpdateStudyRoomTagRequest> tagsToUpdate;
    private List<Long> tagIdsToRemove;
}
