package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.CreateStudyRequest;

import java.util.List;

import static com.jj.swm.domain.study.core.entity.StudyCategory.ALGORITHM;

public class CreateStudyRequestFixture {

    public static CreateStudyRequest create() {
        return CreateStudyRequest.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(ALGORITHM)
                .tags(List.of("test_tag1", "test_tag2"))
                .imageUrls(List.of("http://test_image1.png", "http://test_image2.png"))
                .createRecruitmentPositionRequests(List.of(
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create()
                )).build();
    }

    public static CreateStudyRequest createForNoTagImagesSuccess() {
        return CreateStudyRequest.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(ALGORITHM)
                .createRecruitmentPositionRequests(List.of(
                        CreateRecruitmentPositionRequestFixture.create(),
                        CreateRecruitmentPositionRequestFixture.create()
                )).build();
    }
}
