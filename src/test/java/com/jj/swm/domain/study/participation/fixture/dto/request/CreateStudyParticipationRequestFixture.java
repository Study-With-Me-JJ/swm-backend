package com.jj.swm.domain.study.participation.fixture.dto.request;

import com.jj.swm.domain.study.participation.dto.request.CreateStudyParticipationRequest;
import com.jj.swm.domain.study.participation.entity.embeddable.FileInfo;

import java.util.List;

public class CreateStudyParticipationRequestFixture {

    public static CreateStudyParticipationRequest create() {
        return CreateStudyParticipationRequest.builder()
                .kakaoId("test_kakao_id")
                .coverLetter("test_cover_letter")
                .links(List.of("test_link1", "test_link2"))
                .fileInfo(FileInfo.builder()
                        .fileUrl("test_file_url")
                        .fileName("test_file_name")
                        .build())
                .build();
    }

    public static CreateStudyParticipationRequest createForNoLinkAndFileUrlSuccess() {
        return CreateStudyParticipationRequest.builder()
                .kakaoId("test_kakao_id")
                .coverLetter("test_cover_letter")
                .build();
    }
}
