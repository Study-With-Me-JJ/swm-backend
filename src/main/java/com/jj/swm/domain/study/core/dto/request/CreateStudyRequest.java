package com.jj.swm.domain.study.core.dto.request;

import com.jj.swm.domain.study.core.entity.StudyCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import static com.jj.swm.domain.study.core.constants.StudyConstants.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStudyRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    @Size(max = 300)
    private String openChatUrl;

    @NotNull
    private StudyCategory category;

    @Valid
    @Size(max = IMAGE_LIMIT)
    private List<@Size(max = 50) String> tags;

    @Valid
    @Size(max = TAG_LIMIT)
    private List<@Size(max = 300) String> imageUrls;

    @Valid
    @NotEmpty
    @Size(min = 1, max = RECRUITMENT_POSITION_LIMIT)
    private List<CreateRecruitmentPositionRequest> createRecruitmentPositionRequests;
}
