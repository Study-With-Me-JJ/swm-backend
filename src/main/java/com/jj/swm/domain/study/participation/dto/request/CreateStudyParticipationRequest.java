package com.jj.swm.domain.study.participation.dto.request;

import com.jj.swm.domain.study.participation.entity.embeddable.FileInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import static com.jj.swm.domain.study.participation.constants.StudyParticipationConstants.LINK_LIMIT;

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
    private FileInfo fileInfo;
}
