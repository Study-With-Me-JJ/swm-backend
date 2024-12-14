package com.jj.swm.domain.study.dto.response;

import com.jj.swm.domain.study.dto.StudyPositionAcceptedCountInfo;
import com.jj.swm.domain.study.entity.StudyParticipantStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyRecruitPositionInquiryResponse {

    private Long recruitPositionId;

    private String title;

    private Integer headcount;

    private Integer acceptedCount;

    private StudyParticipantStatus status;

    public static StudyRecruitPositionInquiryResponse of(
            StudyPositionAcceptedCountInfo participantInfo, StudyParticipantStatus status
    ) {
        return StudyRecruitPositionInquiryResponse.builder()
                .recruitPositionId(participantInfo.getId())
                .title(participantInfo.getTitle())
                .headcount(participantInfo.getHeadcount())
                .acceptedCount(participantInfo.getAcceptedCount())
                .status(status)
                .build();
    }
}
