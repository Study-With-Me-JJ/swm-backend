package com.jj.swm.domain.studyroom.core.dto.request.update;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomImageRequest {

    private List<String> imagesToAdd;

    private List<Long> imageIdsToRemove;
}
