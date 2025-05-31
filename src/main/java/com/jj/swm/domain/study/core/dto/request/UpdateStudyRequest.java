package com.jj.swm.domain.study.core.dto.request;

import com.jj.swm.domain.study.core.entity.Study.StudyCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import static com.jj.swm.domain.study.core.constants.StudyConstants.IMAGE_LIMIT;
import static com.jj.swm.domain.study.core.constants.StudyConstants.TAG_LIMIT;

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
    private ModifyTagInfo modifyTagInfo;

    @Valid
    private ModifyImageInfo modifyImageInfo;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ModifyImageInfo {

        @Valid
        @Size(max = IMAGE_LIMIT)
        private List<@Size(max = 300) String> imageUrlsToAdd;

        @Size(max = IMAGE_LIMIT)
        private List<Long> imageIdsToRemove;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ModifyTagInfo {

        @Valid
        @Size(max = TAG_LIMIT)
        private List<@Size(max = 50) String> tagsToAdd;

        @Size(max = TAG_LIMIT)
        private List<Long> tagIdsToRemove;
    }
}
