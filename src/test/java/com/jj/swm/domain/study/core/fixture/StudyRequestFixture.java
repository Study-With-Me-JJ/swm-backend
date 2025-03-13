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
                .tagList(List.of("test_tag1", "test_tag2"))
                .imageUrlList(List.of("http://test_image1.png", "http://test_image2.png"))
                .createRecruitmentPositionRequestList(List.of(
                        createRecruitmentPositionRequest(), createRecruitmentPositionRequest()
                )).build();
    }

    public static CreateStudyRequest createStudyRequestWithoutTagAndImageList() {
        return CreateStudyRequest.builder()
                .title("test_title")
                .content("test_content")
                .openChatUrl("test_open_chat_url")
                .category(StudyCategory.ALGORITHM)
                .createRecruitmentPositionRequestList(List.of(
                        createRecruitmentPositionRequest(), createRecruitmentPositionRequest()
                )).build();
    }

    public static UpdateStudyRequest updateStudyRequest() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(saveStudyTagRequest())
                .saveImageRequest(saveStudyImageRequest())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithoutSaveTagAndImageRequest() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithoutTagAndImageListToAdd() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(saveStudyTagRequestWithoutTagListToAdd())
                .saveImageRequest(saveStudyImageRequestWithoutImageListToAdd())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithoutTagAndImageIdListToRemove() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(saveStudyTagRequestWithoutTagIdListToRemove())
                .saveImageRequest(saveStudyImageRequestWithoutImageIdListToRemove())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithUnderTagLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(saveStudyTagRequestWithUnderTagLimit())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithExceedTagLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveTagRequest(saveStudyTagRequestWithExceedTagLimit())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithUnderImageLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveImageRequest(saveStudyImageRequestWithUnderImageLimit())
                .build();
    }

    public static UpdateStudyRequest updateStudyRequestWithExceedImageLimit() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .saveImageRequest(saveStudyImageRequestWithExceedImageLimit())
                .build();
    }

    private static SaveStudyTagRequest saveStudyTagRequest() {
        return SaveStudyTagRequest.builder()
                .tagListToAdd(List.of("new_test_tag1", "new_test_tag2"))
                .tagIdListToRemove(List.of(1L))
                .build();
    }

    private static SaveStudyTagRequest saveStudyTagRequestWithoutTagListToAdd() {
        return SaveStudyTagRequest.builder()
                .tagIdListToRemove(List.of(1L))
                .build();
    }

    private static SaveStudyTagRequest saveStudyTagRequestWithoutTagIdListToRemove() {
        return SaveStudyTagRequest.builder()
                .tagListToAdd(List.of("new_test_tag1", "new_test_tag2"))
                .build();
    }

    private static SaveStudyTagRequest saveStudyTagRequestWithUnderTagLimit() {
        return SaveStudyTagRequest.builder()
                .tagIdListToRemove(List.of(1L, 2L, 3L))
                .build();
    }

    private static SaveStudyTagRequest saveStudyTagRequestWithExceedTagLimit() {
        return SaveStudyTagRequest.builder()
                .tagListToAdd(List.of(
                        "new_test_tag1", "new_test_tag2", "new_test_tag3",
                        "new_test_tag4", "new_test_tag5", "new_test_tag6",
                        "new_test_tag7", "new_test_tag8", "new_test_tag9"
                )).build();
    }

    private static SaveStudyImageRequest saveStudyImageRequest() {
        return SaveStudyImageRequest.builder()
                .imageUrlListToAdd(List.of("http://new_test_image1.png", "http://new_test_image2.png"))
                .imageIdListToRemove(List.of(1L))
                .build();
    }

    private static SaveStudyImageRequest saveStudyImageRequestWithoutImageListToAdd() {
        return SaveStudyImageRequest.builder()
                .imageIdListToRemove(List.of(1L))
                .build();
    }

    private static SaveStudyImageRequest saveStudyImageRequestWithoutImageIdListToRemove() {
        return SaveStudyImageRequest.builder()
                .imageUrlListToAdd(List.of("http://new_test_image1.png", "http://new_test_image2.png"))
                .build();
    }

    private static SaveStudyImageRequest saveStudyImageRequestWithUnderImageLimit() {
        return SaveStudyImageRequest.builder()
                .imageIdListToRemove(List.of(1L, 2L, 3L))
                .build();
    }

    private static SaveStudyImageRequest saveStudyImageRequestWithExceedImageLimit() {
        return SaveStudyImageRequest.builder()
                .imageUrlListToAdd(List.of(
                        "http://new_test_image1.png", "http://new_test_image2.png", "http://new_test_image3.png",
                        "http://new_test_image4.png", "http://new_test_image5.png", "http://new_test_image6.png",
                        "http://new_test_image7.png", "http://new_test_image8.png", "http://new_test_image9.png"
                )).build();
    }

    public static UpdateStudyStatusRequest updateStudyStatusRequest() {
        return UpdateStudyStatusRequest.builder()
                .status(StudyStatus.INACTIVE)
                .build();
    }

    public static DeleteStudyListRequest deleteStudyListRequest(List<Long> studyIdList) {
        return DeleteStudyListRequest.builder()
                .studyIdList(studyIdList)
                .build();
    }
}
