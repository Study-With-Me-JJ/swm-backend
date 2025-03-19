package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.CreateStudyRequest;
import com.jj.swm.domain.study.core.entity.StudyCategory;
import com.jj.swm.domain.study.recruitmentposition.fixture.dto.request.UpsertRecruitmentPositionRequestFixture;

import java.util.List;

public class CreateStudyRequestFixture {

    public static CreateStudyRequest create() {
        return CreateStudyRequest.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(StudyCategory.ALGORITHM)
                .tags(List.of("test_tag1", "test_tag2"))
                .imageUrls(List.of("http://test_image1.png", "http://test_image2.png"))
                .upsertRecruitmentPositionRequests(List.of(
                        UpsertRecruitmentPositionRequestFixture.create(),
                        UpsertRecruitmentPositionRequestFixture.create()
                )).build();
    }

    public static CreateStudyRequest createForNoTagImagesSuccess() {
        return CreateStudyRequest.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(StudyCategory.ALGORITHM)
                .upsertRecruitmentPositionRequests(List.of(
                        UpsertRecruitmentPositionRequestFixture.create(),
                        UpsertRecruitmentPositionRequestFixture.create()
                )).build();
    }
}
