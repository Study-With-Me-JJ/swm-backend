package com.jj.swm.domain.studyroom.review.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.review.dto.response.GetStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.review.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.review.entity.StudyRoomReviewImage;
import com.jj.swm.domain.studyroom.review.entity.StudyRoomReviewReply;
import com.jj.swm.domain.studyroom.core.fixture.StudyRoomFixture;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.review.repository.StudyRoomReviewImageRepository;
import com.jj.swm.domain.studyroom.review.repository.StudyRoomReviewReplyRepository;
import com.jj.swm.domain.studyroom.review.repository.StudyRoomReviewRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.fixture.UserFixture;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StudyRoomReviewQueryServiceIntegrationTest extends IntegrationContainerSupporter {

    // Target Service Bean
    @Autowired private StudyRoomReviewQueryService queryService;

    // Repository Bean
    @Autowired private StudyRoomReviewRepository reviewRepository;
    @Autowired private StudyRoomReviewReplyRepository reviewReplyRepository;

    // Repository Bean For Test
    @Autowired private StudyRoomRepository studyRoomRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private StudyRoomReviewImageRepository reviewImageRepository;

    private StudyRoom studyRoom;

    @BeforeEach
    void setUp(){
        User roomAdmin = UserFixture.createRoomAdmin();
        userRepository.save(roomAdmin);

        studyRoom = studyRoomRepository.save(StudyRoomFixture.createStudyRoom(roomAdmin));

        User reviewUser1 = UserFixture.createUser();
        User reviewUser2 = UserFixture.createUser();

        userRepository.saveAll(List.of(reviewUser1, reviewUser2));

        StudyRoomReview noneImageReview = StudyRoomReview.of(
                "content",
                5,
                studyRoom,
                reviewUser1
        );

        StudyRoomReview imageReview = StudyRoomReview.of(
                "content",
                5,
                studyRoom,
                reviewUser2
        );

        reviewRepository.saveAll(List.of(noneImageReview, imageReview));

        StudyRoomReviewImage image = StudyRoomReviewImage.builder()
                .imageUrl("image1")
                .studyRoomReview(imageReview)
                .build();

        reviewImageRepository.save(image);

        StudyRoomReviewReply reply = StudyRoomReviewReply.of(
                "reply",
                noneImageReview,
                reviewUser1
        );

        reviewReplyRepository.save(reply);
    }

    @Test
    @DisplayName("스터디 룸 이용후기 목록 조회에 성공한다.")
    public void studyRoomReview_getStudyRoomReviews_Success() {
        // when
        PageResponse<GetStudyRoomReviewResponse> response
                = queryService.getStudyRoomReviews(studyRoom.getId(), false, 0);

        // then
        assertThat(response.getData().size()).isEqualTo(2); // (noneImageReview, imageReview))
        assertThat(response.getData().getFirst().getImageUrls().size()).isEqualTo(1); // 최신순 (imageReview)
        assertThat(response.getData().get(1).getImageUrls()).isEmpty(); // 최신순 (noneImageReview)
        assertThat(response.getData().get(1).getReplies().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("스터디 룸 이용후기 이미지만 있는 목록 조회에 성공한다.")
    public void studyRoomReview_getStudyRoomReviews_thenReturnOnlyImageReviews_Success() {
        // when
        PageResponse<GetStudyRoomReviewResponse> response
                = queryService.getStudyRoomReviews(1L, true, 0);

        // then
        assertThat(response.getData().size()).isEqualTo(1); // (noneImageReview, imageReview))
        assertThat(response.getData().getFirst().getImageUrls().size()).isEqualTo(1); // 최신순 (imageReview)
    }
}
