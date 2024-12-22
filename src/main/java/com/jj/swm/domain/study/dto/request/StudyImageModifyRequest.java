package com.jj.swm.domain.study.dto.request;


import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyImageModifyRequest {

    @Size(max = 100)
    private List<String> newImageUrls;

    @Size(max = 100)
    private List<Long> deletedImageIds;
}
