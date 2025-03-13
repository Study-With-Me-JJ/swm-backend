package com.jj.swm.domain.study.core.fixture.request;

import com.jj.swm.domain.study.core.dto.request.*;
import com.jj.swm.domain.study.core.entity.StudyCategory;
import com.jj.swm.domain.study.core.entity.StudyStatus;

import java.util.List;

import static com.jj.swm.domain.study.recruitmentposition.fixture.request.RecruitmentPositionRequestFixture.buildCreateRecruitmentPositionRequest;

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

    public static UpdateStudyRequest buildUpdateStudyRequest() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(buildSaveStudyTagRequest())
                .saveImageRequest(buildSaveStudyImageRequest())
                .build();
    }

    public static UpdateStudyRequest buildUpdateStudyRequestWithoutSaveTagAndImageRequest() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .build();
    }

    public static UpdateStudyRequest buildUpdateStudyRequestWithoutTagAndImageListToAdd() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(buildSaveStudyTagRequestWithoutTagListToAdd())
                .saveImageRequest(buildSaveStudyImageRequestWithoutImageListToAdd())
                .build();
    }

    public static UpdateStudyRequest buildUpdateStudyRequestWithoutTagAndImageIdListToRemove() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(buildSaveStudyTagRequestWithoutTagIdListToRemove())
                .saveImageRequest(buildSaveStudyImageRequestWithoutImageIdListToRemove())
                .build();
    }

    public static UpdateStudyRequest buildUpdateStudyRequestWithUnderTagLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(buildSaveStudyTagRequestWithUnderTagLimit())
                .build();
    }

    public static UpdateStudyRequest buildUpdateStudyRequestWithExceedTagLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(buildSaveStudyTagRequestWithExceedTagLimit())
                .build();
    }

    public static UpdateStudyRequest buildUpdateStudyRequestWithUnderImageLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveImageRequest(buildSaveStudyImageRequestWithUnderImageLimit())
                .build();
    }

    public static UpdateStudyRequest buildUpdateStudyRequestWithExceedImageLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveImageRequest(buildSaveStudyImageRequestWithExceedImageLimit())
                .build();
    }

    private static SaveStudyTagRequest buildSaveStudyTagRequest() {
        return SaveStudyTagRequest.builder()
                .tagListToAdd(List.of("new_test_tag1", "new_test_tag2"))
                .tagIdListToRemove(List.of(1L))
                .build();
    }

    private static SaveStudyTagRequest buildSaveStudyTagRequestWithoutTagListToAdd() {
        return SaveStudyTagRequest.builder()
                .tagIdListToRemove(List.of(1L))
                .build();
    }

    private static SaveStudyTagRequest buildSaveStudyTagRequestWithoutTagIdListToRemove() {
        return SaveStudyTagRequest.builder()
                .tagListToAdd(List.of("new_test_tag1", "new_test_tag2"))
                .build();
    }

    private static SaveStudyTagRequest buildSaveStudyTagRequestWithUnderTagLimit() {
        return SaveStudyTagRequest.builder()
                .tagIdListToRemove(List.of(1L, 2L, 3L))
                .build();
    }

    private static SaveStudyTagRequest buildSaveStudyTagRequestWithExceedTagLimit() {
        return SaveStudyTagRequest.builder()
                .tagListToAdd(List.of(
                        "new_test_tag1", "new_test_tag2", "new_test_tag3",
                        "new_test_tag4", "new_test_tag5", "new_test_tag6",
                        "new_test_tag7", "new_test_tag8", "new_test_tag9"
                )).build();
    }

    private static SaveStudyImageRequest buildSaveStudyImageRequest() {
        return SaveStudyImageRequest.builder()
                .imageUrlListToAdd(List.of("http://new_test_image1.png", "http://new_test_image2.png"))
                .imageIdListToRemove(List.of(1L))
                .build();
    }

    private static SaveStudyImageRequest buildSaveStudyImageRequestWithoutImageListToAdd() {
        return SaveStudyImageRequest.builder()
                .imageIdListToRemove(List.of(1L))
                .build();
    }

    private static SaveStudyImageRequest buildSaveStudyImageRequestWithoutImageIdListToRemove() {
        return SaveStudyImageRequest.builder()
                .imageUrlListToAdd(List.of("http://new_test_image1.png", "http://new_test_image2.png"))
                .build();
    }

    private static SaveStudyImageRequest buildSaveStudyImageRequestWithUnderImageLimit() {
        return SaveStudyImageRequest.builder()
                .imageIdListToRemove(List.of(1L, 2L, 3L))
                .build();
    }

    private static SaveStudyImageRequest buildSaveStudyImageRequestWithExceedImageLimit() {
        return SaveStudyImageRequest.builder()
                .imageUrlListToAdd(List.of(
                        "http://new_test_image1.png", "http://new_test_image2.png", "http://new_test_image3.png",
                        "http://new_test_image4.png", "http://new_test_image5.png", "http://new_test_image6.png",
                        "http://new_test_image7.png", "http://new_test_image8.png", "http://new_test_image9.png"
                )).build();
    }

    public static UpdateStudyStatusRequest buildUpdateStudyStatusRequest() {
        return UpdateStudyStatusRequest.builder()
                .status(StudyStatus.INACTIVE)
                .build();
    }

    public static DeleteStudyListRequest buildDeleteStudyListRequest(List<Long> studyIdList) {
        return DeleteStudyListRequest.builder()
                .studyIdList(studyIdList)
                .build();
    }
}
