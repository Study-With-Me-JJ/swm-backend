package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomReviewReplyResponse;
import com.jj.swm.domain.studyroom.dto.response.DeleteStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.dto.response.UpdateStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomReview;
import com.jj.swm.domain.studyroom.entity.StudyRoomReviewReply;
import com.jj.swm.domain.studyroom.repository.StudyRoomRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomReviewImageRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomReviewReplyRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomReviewRepository;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
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
    private final StudyRoomReviewImageRepository reviewImageRepository;

    @Transactional
    public CreateStudyRoomReviewResponse createReview(
            CreateStudyRoomReviewRequest request,
            Long studyRoomId,
            UUID userId
    ) {
        // 이용 내역 검증 로직 필요
        StudyRoom studyRoom = validateStudyRoomWithLock(studyRoomId);
        User user = userRepository.getReferenceById(userId);

        StudyRoomReview studyRoomReview = StudyRoomReview.of(
                request.getComment(),
                request.getRating(),
                studyRoom,
                user
        );

        reviewRepository.save(studyRoomReview);

        reviewImageRepository.batchInsert(request.getImageUrls(), studyRoomReview);

        studyRoom.addReviewStudyRoom(request.getRating());

        return CreateStudyRoomReviewResponse.of(studyRoomReview, request.getImageUrls());
    }

    @Transactional
    public UpdateStudyRoomReviewResponse updateReview(
            UpdateStudyRoomReviewRequest request,
            Long studyRoomId,
            Long studyRoomReviewId,
            UUID userId
    ) {
        StudyRoomReview studyRoomReview = validateReviewWithUserId(studyRoomReviewId, userId);
        StudyRoom studyRoom = validateStudyRoomWithLock(studyRoomId);

        studyRoom.updateAverageRating(studyRoomReview.getRating(), request.getRating());

        studyRoomReview.modifyReview(request);

        return UpdateStudyRoomReviewResponse.of(studyRoomReview, studyRoom.getAverageRating());
    }

    @Transactional
    public DeleteStudyRoomReviewResponse deleteReview(
            Long studyRoomId,
            Long studyRoomReviewId,
            UUID userId
    ) {
        StudyRoomReview studyRoomReview = validateReviewWithUserId(studyRoomReviewId, userId);
        StudyRoom studyRoom = validateStudyRoomWithLock(studyRoomId);

        studyRoom.deleteReviewStudyRoom(studyRoomReview.getRating());

        reviewReplyRepository.deleteAllByStudyRoomReviewId(studyRoomReviewId);
        reviewImageRepository.deleteAllByStudyRoomReviewId(studyRoomReviewId);

        reviewRepository.delete(studyRoomReview);

        return DeleteStudyRoomReviewResponse.of(studyRoomReviewId, studyRoom.getAverageRating());
    }

    @Transactional
    public CreateStudyRoomReviewReplyResponse createReviewReply(
            CreateStudyRoomReviewReplyRequest request,
            Long studyRoomReviewId,
            UUID userId
    ){
        StudyRoomReview studyRoomReview = validateReviewAndGetStudyRoomAndUser(studyRoomReviewId, userId);

        User user = userRepository.getReferenceById(userId);

        StudyRoomReviewReply studyRoomReviewReply = StudyRoomReviewReply.of(request.getReply(), studyRoomReview, user);

        reviewReplyRepository.save(studyRoomReviewReply);

        return CreateStudyRoomReviewReplyResponse.from(studyRoomReviewReply);
    }

    @Transactional
    public void updateReviewReply(
            UpdateStudyRoomReviewReplyRequest request,
            Long studyRoomReviewReplyId,
            UUID userId
    ) {
        StudyRoomReviewReply studyRoomReviewReply
                = validateReviewReplyWithUserId(studyRoomReviewReplyId, userId);

        studyRoomReviewReply.modifyReply(request.getReply());
    }

    @Transactional
    public void deleteReviewReply(Long studyRoomReviewReplyId, UUID userId) {
        StudyRoomReviewReply studyRoomReviewReply = validateReviewReplyWithUserId(studyRoomReviewReplyId, userId);

        reviewReplyRepository.delete(studyRoomReviewReply);
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
