package com.jj.swm.domain.study.core.dto.request;

import com.jj.swm.domain.study.recruitmentposition.dto.request.AddRecruitmentPositionRequest;
import com.jj.swm.domain.study.core.entity.StudyCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddStudyRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String openChatUrl;

    @NotNull
    private StudyCategory category;

    @Size(max = 100)
    private List<String> tagList;

    @Size(max = 100)
    private List<String> imageUrlList;

    @Valid
    @NotEmpty
    @Size(max = 100)
    private List<AddRecruitmentPositionRequest> addRecruitmentPositionRequestList;
}
