package com.jj.swm.domain.studyroom.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStudyRoomReviewReplyRequest {

    @NotBlank
    private String reply;
}
