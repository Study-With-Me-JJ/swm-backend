package com.jj.swm.domain.study.core.fixture.request;

import com.jj.swm.domain.study.core.dto.request.CreateStudyRequest;
import com.jj.swm.domain.study.core.entity.StudyCategory;

import java.util.List;

import static com.jj.swm.domain.study.core.fixture.request.RecruitmentPositionRequestFixture.buildCreateRecruitmentPositionRequest;

public class StudyRequestFixture {

    public static CreateStudyRequest buildCreateStudyRequest() {
        return CreateStudyRequest.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(StudyCategory.ALGORITHM)
                .tagList(List.of("test_tag1", "test_tag2"))
                .imageUrlList(List.of("http://test_image1.png", "http://test_image2.png"))
                .createRecruitmentPositionRequestList(List.of(
                        buildCreateRecruitmentPositionRequest(), buildCreateRecruitmentPositionRequest()
                )).build();
    }

    public static CreateStudyRequest buildCreateStudyRequestWithoutTagAndImageList() {
        return CreateStudyRequest.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(StudyCategory.ALGORITHM)
                .createRecruitmentPositionRequestList(List.of(
                        buildCreateRecruitmentPositionRequest(), buildCreateRecruitmentPositionRequest()
                )).build();
    }
}
