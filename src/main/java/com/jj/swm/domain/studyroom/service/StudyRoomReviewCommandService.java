package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomReviewCreateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomReviewReplyCreateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomReviewReplyUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomReviewUpdateRequest;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomReviewCreateResponse;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomReviewReplyCreateResponse;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomReviewUpdateResponse;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.entity.StudyRoomReviewReply;
import com.jj.swm.domain.studyroom.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomReviewReplyRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomReviewRepository;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyRoomReviewCommandService {

    private final UserRepository userRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomReviewRepository reviewRepository;
    private final StudyRoomReviewReplyRepository reviewReplyRepository;

    @Transactional
    public StudyRoomReviewCreateResponse createReview(StudyRoomReviewCreateRequest request, UUID userId) {
        // 이용 내역 검증 로직 필요
        StudyRoom studyRoom = validateStudyRoomWithLock(request.getStudyRoomId());
        User user = userRepository.getReferenceById(userId);

        StudyRoomReview studyRoomReview = StudyRoomReview.of(
                request.getComment(),
                request.getRating(),
                studyRoom,
                user
        );

        reviewRepository.save(studyRoomReview);

        studyRoom.addReviewStudyRoom(request.getRating());

        return StudyRoomReviewCreateResponse.from(studyRoomReview);
    }

    @Transactional
    public StudyRoomReviewUpdateResponse updateReview(StudyRoomReviewUpdateRequest request, UUID userId) {
        StudyRoomReview studyRoomReview = validateReviewWithUserId(request.getStudyRoomReviewId(), userId);
        StudyRoom studyRoom = validateStudyRoomWithLock(request.getStudyRoomId());

        studyRoom.updateAverageRating(studyRoomReview.getRating(), request.getRating());

        studyRoomReview.modifyReview(request);

        return StudyRoomReviewUpdateResponse.of(studyRoomReview, studyRoom.getAverageRating());
    }

    @Transactional
    public StudyRoomReviewReplyCreateResponse createReviewReply(StudyRoomReviewReplyCreateRequest request, UUID userId){
        StudyRoomReview studyRoomReview = validateReviewAndGetStudyRoomAndUser(request.getStudyRoomReviewId(), userId);

        User user = userRepository.getReferenceById(userId);

        StudyRoomReviewReply studyRoomReviewReply = StudyRoomReviewReply.of(request.getReply(), studyRoomReview, user);

        reviewReplyRepository.save(studyRoomReviewReply);

        return StudyRoomReviewReplyCreateResponse.from(studyRoomReviewReply);
    }

    @Transactional
    public void updateReviewReply(StudyRoomReviewReplyUpdateRequest request, UUID userId) {
        StudyRoomReviewReply studyRoomReviewReply
                = validateReviewReplyWithUserId(request.getStudyRoomReviewReplyId(), userId);

        studyRoomReviewReply.modifyReply(request.getReply());
    }

    private StudyRoom validateStudyRoomWithLock(Long studyRoomId) {
        return studyRoomRepository.findByIdWithLock(studyRoomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoom Not Found"));
    }

    private StudyRoomReview validateReviewWithUserId(Long studyRoomReviewId, UUID userId) {
        return reviewRepository.findByIdAndUserId(studyRoomReviewId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReview Not Found"));
    }

    private StudyRoomReview validateReviewAndGetStudyRoomAndUser(Long studyRoomReviewId, UUID userId) {
        return reviewRepository.findByStudyRoomReviewWithNativeQuery(studyRoomReviewId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReview Not Found"));
    }

    private StudyRoomReviewReply validateReviewReplyWithUserId(Long studyRoomReviewReplyId, UUID userId) {
        return reviewReplyRepository.findByIdAndUserId(studyRoomReviewReplyId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomReviewReply Not Found"));
    }
}
