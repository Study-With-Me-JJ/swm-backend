package com.jj.swm.domain.studyroom.review.service;

import com.jj.swm.domain.studyroom.review.dto.response.GetStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.review.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.review.repository.StudyRoomReviewRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.PageSize;
import lombok.RequiredArgsConstructor;
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

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomReviewResponse> getStudyRoomReviews(
            Long studyRoomId,
            boolean onlyImage,
            int pageNo
    ) {
        Pageable pageable = PageRequest.of(pageNo, PageSize.StudyRoomReview, Sort.by("id").descending());

        Page<StudyRoomReview> pagedReviews;

        if (onlyImage) {
            pagedReviews = reviewRepository.findPagedReviewWithOnlyImageAndUserByStudyRoomId(studyRoomId, pageable);
        } else {
            pagedReviews = reviewRepository.findPagedReviewWithUserByStudyRoomId(studyRoomId, pageable);
        }

        // 추후, 본인 글 수정 가능 표시 추가 가능
        return PageResponse.of(
                pagedReviews, GetStudyRoomReviewResponse::from
        );
    }
}
