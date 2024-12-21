package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomReviewReplyResponse;
import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.repository.StudyRoomReviewRepository;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyRoomReviewQueryService {

    private final StudyRoomReviewRepository reviewRepository;

    @Value("${studyroom.review.page.size}")
    private int reviewPageSize;

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomReviewResponse> getStudyRoomReviews(
            Long studyRoomId,
            boolean onlyImage,
            int pageNo
    ) {
        Pageable pageable = PageRequest.of(pageNo, reviewPageSize, Sort.by("id").descending());

        Page<StudyRoomReview> pagedReviews;

        if (onlyImage) {
            pagedReviews = reviewRepository.findPagedReviewWithOnlyImageAndUserByStudyRoomId(studyRoomId, pageable);
        } else {
            pagedReviews = reviewRepository.findPagedReviewWithUserByStudyRoomId(studyRoomId, pageable);
        }

        // 추후, 본인 글 수정 가능 표시 추가 가능
        return PageResponse.of(
                pagedReviews, review -> GetStudyRoomReviewResponse.of(review, review.getReplies())
        );
    }
}
