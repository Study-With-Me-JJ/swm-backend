package com.jj.swm.domain.study.core.fixture.dto.request;

import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest.ModifyImageInfo;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest.ModifyTagInfo;

import java.util.List;

import static com.jj.swm.domain.study.core.entity.Study.StudyCategory.DEVELOPMENT;


public class UpdateStudyRequestFixture {

    public static UpdateStudyRequest create() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(DEVELOPMENT)
                .modifyTagInfo(ModifyTagInfo.builder()
                        .tagsToAdd(List.of("new_test_tag1", "new_test_tag2"))
                        .tagIdsToRemove(List.of(1L))
                        .build())
                .modifyImageInfo(ModifyImageInfo.builder()
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
                .category(DEVELOPMENT)
                .build();
    }

    public static UpdateStudyRequest createForNoTagAndImagesToAddSuccess() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(DEVELOPMENT)
                .modifyTagInfo(ModifyTagInfo.builder()
                        .tagIdsToRemove(List.of(1L))
                        .build())
                .modifyImageInfo(ModifyImageInfo.builder()
                        .imageIdsToRemove(List.of(1L))
                        .build())
                .build();
    }

    public static UpdateStudyRequest createFroNoTagAndImageIdsToRemoveSuccess() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(DEVELOPMENT)
                .modifyTagInfo(ModifyTagInfo.builder()
                        .tagsToAdd(List.of("new_test_tag1", "new_test_tag2"))
                        .build())
                .modifyImageInfo(ModifyImageInfo.builder()
                        .imageUrlsToAdd(List.of("http://new_test_image1.png", "http://new_test_image2.png"))
                        .build())
                .build();
    }

    public static UpdateStudyRequest createForWrongTagIdToRemove() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(DEVELOPMENT)
                .modifyTagInfo(ModifyTagInfo.builder()
                        .tagIdsToRemove(List.of(1L, 2L, 3L))
                        .build())
                .build();
    }

    public static UpdateStudyRequest createForExceedTagLimitFail() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(DEVELOPMENT)
                .modifyTagInfo(ModifyTagInfo.builder()
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
                .category(DEVELOPMENT)
                .modifyImageInfo(ModifyImageInfo.builder()
                        .imageIdsToRemove(List.of(1L, 2L, 3L))
                        .build())
                .build();
    }

    public static UpdateStudyRequest createForExceedImageLimitFail() {
        return UpdateStudyRequest.builder()
                .title("new_test_title")
                .content("new_test_content")
                .openChatUrl("new_test_openChatUrl")
                .category(DEVELOPMENT)
                .modifyImageInfo(ModifyImageInfo.builder()
                        .imageUrlsToAdd(List.of(
                                "http://newTest_image1.png", "http://newTest_image2.png", "http://newTest_image3.png",
                                "http://newTest_image4.png", "http://newTest_image5.png", "http://newTest_image6.png",
                                "http://newTest_image7.png", "http://newTest_image8.png", "http://newTest_image9.png"
                        )).build())
                .build();
    }
}
