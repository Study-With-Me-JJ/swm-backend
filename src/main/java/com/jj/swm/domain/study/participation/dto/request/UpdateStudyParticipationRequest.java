package com.jj.swm.domain.study.participation.dto.request;

import com.jj.swm.domain.study.participation.entity.embeddable.FileInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyParticipationRequest {

    @NotBlank
    @Size(max = 50)
    private String kakaoId;

    @NotBlank
    private String coverLetter;

    @Valid
    private ModifyStudyParticipationLinkRequest modifyLinkRequest;

    @Valid
    private FileInfo fileInfo;
}
