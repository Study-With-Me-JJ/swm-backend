package com.jj.swm.domain.study.recruitmentposition.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import static com.jj.swm.domain.study.recruitmentposition.constants.StudyParticipationConstants.FILE_LIMIT;
import static com.jj.swm.domain.study.recruitmentposition.constants.StudyParticipationConstants.LINK_LIMIT;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateStudyParticipationRequest {

    @NotBlank
    @Size(max = 50)
    private String kakaoId;

    @NotBlank
    private String coverLetter;

    @Valid
    @Size(max = LINK_LIMIT)
    private List<@Size(max = 300) String> links;

    @Valid
    @Size(max = FILE_LIMIT)
    private List<@Size(max = 300) String> fileUrls;
}
