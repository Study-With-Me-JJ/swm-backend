package com.jj.swm.domain.studyroom.core.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteStudyRoomsRequest {

    @NotEmpty
    private List<Long> studyRoomIds;
}
