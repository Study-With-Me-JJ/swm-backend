package com.jj.swm.domain.study.recruitmentposition.fixture.dto.request;

import com.jj.swm.domain.study.recruitmentposition.dto.request.CreateStudyParticipationRequest;

import java.util.List;

public class CreateStudyParticipationRequestFixture {

    public static CreateStudyParticipationRequest create() {
        return CreateStudyParticipationRequest.builder()
                .kakaoId("test_kakao_id")
                .coverLetter("test_cover_letter")
                .links(List.of("test_link1","test_link2"))
                .fileUrls(List.of("test_file_url1", "test_file_url2"))
                .build();
    }

    public static CreateStudyParticipationRequest createForNoLinkAndFileUrlSuccess() {
        return CreateStudyParticipationRequest.builder()
                .kakaoId("test_kakao_id")
                .coverLetter("test_cover_letter")
                .build();
    }
}
