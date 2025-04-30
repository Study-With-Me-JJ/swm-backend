package com.jj.swm.domain.study.core.dto.request;

import com.jj.swm.domain.study.core.entity.StudyCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateStudyRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String content;

    @Size(max = 300)
    private String openChatUrl;

    @NotNull
    private StudyCategory category;

    @Valid
    private ModifyStudyTagRequest modifyTagRequest;

    @Valid
    private ModifyStudyImageRequest modifyImageRequest;
}
