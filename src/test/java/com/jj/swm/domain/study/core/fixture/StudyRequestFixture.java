package com.jj.swm.domain.study.core.fixture;

import com.jj.swm.domain.study.core.dto.request.*;
import com.jj.swm.domain.study.core.entity.StudyCategory;
import com.jj.swm.domain.study.core.entity.StudyStatus;

import java.util.List;

import static com.jj.swm.domain.study.recruitmentposition.fixture.RecruitmentPositionRequestFixture.createRecruitmentPositionRequest;

public class StudyRequestFixture {

    public static CreateStudyRequest createStudyRequest() {
        return CreateStudyRequest.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(StudyCategory.ALGORITHM)
                .tags(List.of("test_tag1", "test_tag2"))
                .imageUrls(List.of("http://test_image1.png", "http://test_image2.png"))
                .createRecruitmentPositionRequests(List.of(
                        createRecruitmentPositionRequest(), createRecruitmentPositionRequest()
                )).build();
    }

    public static CreateStudyRequest createStudyRequestWithoutTagAndImages() {
        return CreateStudyRequest.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(StudyCategory.ALGORITHM)
                .createRecruitmentPositionRequests(List.of(
                        createRecruitmentPositionRequest(), createRecruitmentPositionRequest()
                )).build();
    }

    public static UpdateStudyRequest updateStudyRequest() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(modifyStudyTagRequest())
                .modifyImageRequest(modifyStudyImageRequest())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithoutModifyTagAndImageRequest() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithoutTagAndImagesToAdd() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(modifyStudyTagRequestWithoutTagsToAdd())
                .modifyImageRequest(modifyStudyImageRequestWithoutImagesToAdd())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithoutTagAndImageIdsToRemove() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(modifyStudyTagRequestWithoutTagIdsToRemove())
                .modifyImageRequest(modifyStudyImageRequestWithoutImageIdsToRemove())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithUnderTagLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(modifyStudyTagRequestWithUnderTagLimit())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithExceedTagLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(modifyStudyTagRequestWithExceedTagLimit())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithUnderImageLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyImageRequest(modifyStudyImageRequestWithUnderImageLimit())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithExceedImageLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyImageRequest(modifyStudyImageRequestWithExceedImageLimit())
                .build();
    }

    public static UpdateStudyStatusRequest updateStudyStatusRequest() {
        return UpdateStudyStatusRequest.builder()
                .status(StudyStatus.INACTIVE)
                .build();
    }

    public static DeleteStudiesRequest deleteStudiesRequest(List<Long> studyIds) {
        return DeleteStudiesRequest.builder()
                .studyIds(studyIds)
                .build();
    }

    private static ModifyStudyTagRequest modifyStudyTagRequest() {
        return ModifyStudyTagRequest.builder()
                .tagsToAdd(List.of("new_test_tag1", "new_test_tag2"))
                .tagIdsToRemove(List.of(1L))
                .build();
    }

    private static ModifyStudyTagRequest modifyStudyTagRequestWithoutTagsToAdd() {
        return ModifyStudyTagRequest.builder()
                .tagIdsToRemove(List.of(1L))
                .build();
    }

    private static ModifyStudyTagRequest modifyStudyTagRequestWithoutTagIdsToRemove() {
        return ModifyStudyTagRequest.builder()
                .tagsToAdd(List.of("new_test_tag1", "new_test_tag2"))
                .build();
    }

    private static ModifyStudyTagRequest modifyStudyTagRequestWithUnderTagLimit() {
        return ModifyStudyTagRequest.builder()
                .tagIdsToRemove(List.of(1L, 2L, 3L))
                .build();
    }

    private static ModifyStudyTagRequest modifyStudyTagRequestWithExceedTagLimit() {
        return ModifyStudyTagRequest.builder()
                .tagsToAdd(List.of(
                        "new_test_tag1", "new_test_tag2", "new_test_tag3",
                        "new_test_tag4", "new_test_tag5", "new_test_tag6",
                        "new_test_tag7", "new_test_tag8", "new_test_tag9"
                )).build();
    }

    private static ModifyStudyImageRequest modifyStudyImageRequest() {
        return ModifyStudyImageRequest.builder()
                .imageUrlsToAdd(List.of("http://new_test_image1.png", "http://new_test_image2.png"))
                .imageIdsToRemove(List.of(1L))
                .build();
    }

    private static ModifyStudyImageRequest modifyStudyImageRequestWithoutImagesToAdd() {
        return ModifyStudyImageRequest.builder()
                .imageIdsToRemove(List.of(1L))
                .build();
    }

    private static ModifyStudyImageRequest modifyStudyImageRequestWithoutImageIdsToRemove() {
        return ModifyStudyImageRequest.builder()
                .imageUrlsToAdd(List.of("http://new_test_image1.png", "http://new_test_image2.png"))
                .build();
    }

    private static ModifyStudyImageRequest modifyStudyImageRequestWithUnderImageLimit() {
        return ModifyStudyImageRequest.builder()
                .imageIdsToRemove(List.of(1L, 2L, 3L))
                .build();
    }

    private static ModifyStudyImageRequest modifyStudyImageRequestWithExceedImageLimit() {
        return ModifyStudyImageRequest.builder()
                .imageUrlsToAdd(List.of(
                        "http://new_test_image1.png", "http://new_test_image2.png", "http://new_test_image3.png",
                        "http://new_test_image4.png", "http://new_test_image5.png", "http://new_test_image6.png",
                        "http://new_test_image7.png", "http://new_test_image8.png", "http://new_test_image9.png"
                )).build();
    }
}
