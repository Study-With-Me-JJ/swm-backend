package com.jj.swm.domain.study.dto.core.request;

import com.jj.swm.domain.study.dto.recruitmentposition.request.RecruitPositionCreateRequest;
import com.jj.swm.domain.study.entity.core.StudyCategory;
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
public class StudyCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String openChatUrl;

    @NotNull
    private StudyCategory category;

    @Size(max = 100)
    private List<String> tags;

    @Size(max = 100)
    private List<String> imageUrls;

    @Valid
    @NotEmpty
    @Size(max = 100)
    private List<RecruitPositionCreateRequest> recruitPositionCreateRequests;
}
