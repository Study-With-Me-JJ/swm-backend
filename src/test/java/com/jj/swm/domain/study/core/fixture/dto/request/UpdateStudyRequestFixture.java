package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.ModifyStudyImageRequest;
import com.jj.swm.domain.study.core.dto.request.ModifyStudyTagRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest;
import com.jj.swm.domain.study.core.entity.StudyCategory;

import java.util.List;

public class UpdateStudyRequestFixture {

    public static UpdateStudyRequest create() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(ModifyStudyTagRequest.builder()
                        .tagsToAdd(List.of("new_test_tag1", "new_test_tag2"))
                        .tagIdsToRemove(List.of(1L))
                        .build())
                .modifyImageRequest(ModifyStudyImageRequest.builder()
                        .imageUrlsToAdd(List.of("http://new_test_image1.png", "http://new_test_image2.png"))
                        .imageIdsToRemove(List.of(1L))
                        .build())
                .build();
    }

    public static UpdateStudyRequest createForNoModifyTagAndImageRequestSuccess() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .build();
    }

    public static UpdateStudyRequest createForNoTagAndImagesToAddSuccess() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(ModifyStudyTagRequest.builder()
                        .tagIdsToRemove(List.of(1L))
                        .build())
                .modifyImageRequest(ModifyStudyImageRequest.builder()
                        .imageIdsToRemove(List.of(1L))
                        .build())
                .build();
    }

    public static UpdateStudyRequest createFroNoTagAndImageIdsToRemoveSuccess() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(ModifyStudyTagRequest.builder()
                        .tagsToAdd(List.of("new_test_tag1", "new_test_tag2"))
                        .build())
                .modifyImageRequest(ModifyStudyImageRequest.builder()
                        .imageUrlsToAdd(List.of("http://new_test_image1.png", "http://new_test_image2.png"))
                        .build())
                .build();
    }

    public static UpdateStudyRequest createForWrongTagIdToRemove() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(ModifyStudyTagRequest.builder()
                        .tagIdsToRemove(List.of(1L, 2L, 3L))
                        .build())
                .build();
    }

    public static UpdateStudyRequest createForExceedTagLimitFail() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyTagRequest(ModifyStudyTagRequest.builder()
                        .tagsToAdd(List.of(
                                "new_test_tag1", "new_test_tag2", "new_test_tag3",
                                "new_test_tag4", "new_test_tag5", "new_test_tag6",
                                "new_test_tag7", "new_test_tag8", "new_test_tag9"
                        )).build())
                .build();
    }

    public static UpdateStudyRequest createForWrongImageIdToRemove() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyImageRequest(ModifyStudyImageRequest.builder()
                        .imageIdsToRemove(List.of(1L, 2L, 3L))
                        .build())
                .build();
    }

    public static UpdateStudyRequest createForExceedImageLimitFail() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(StudyCategory.DEVELOPMENT)
                .modifyImageRequest(ModifyStudyImageRequest.builder()
                        .imageUrlsToAdd(List.of(
                                "http://newTest_image1.png", "http://newTest_image2.png", "http://newTest_image3.png",
                                "http://newTest_image4.png", "http://newTest_image5.png", "http://newTest_image6.png",
                                "http://newTest_image7.png", "http://newTest_image8.png", "http://newTest_image9.png"
                        )).build())
                .build();
    }
}
