package com.jj.swm.domain.study.core.dto.component;

import com.jj.swm.domain.study.core.entity.StudyTag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagInfo {

    private Long tagId;

    private String name;

    public static TagInfo from(StudyTag studyTag) {
        return TagInfo.builder()
                .tagId(studyTag.getId())
                .name(studyTag.getName())
                .build();
    }
}
