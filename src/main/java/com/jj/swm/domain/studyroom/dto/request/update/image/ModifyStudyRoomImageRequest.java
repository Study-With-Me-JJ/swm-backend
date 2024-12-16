package com.jj.swm.domain.studyroom.dto.request.update.image;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyStudyRoomImageRequest {

    private List<String> imagesToAdd;
    private List<UpdateStudyRoomImageRequest> imagesToUpdate;
    private List<Long> imageIdsToRemove;
}
