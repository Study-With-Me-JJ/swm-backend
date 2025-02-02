package com.jj.swm.domain.study.dto.request;

import com.jj.swm.domain.study.entity.StudyCategory;
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

    private String thumbnail;

    @Size(max = 100)
    private List<String> tags;

    @Size(max = 100)
    private List<String> imageUrls;

    @NotEmpty
    @Size(max = 100)
    private List<RecruitPositionUpsertRequest> recruitPositionCreateRequests;
}
