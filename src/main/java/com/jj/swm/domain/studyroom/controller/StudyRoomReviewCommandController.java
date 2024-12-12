package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomReviewCreateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomReviewReplyCreateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomReviewReplyUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomReviewUpdateRequest;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomReviewCreateResponse;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomReviewReplyCreateResponse;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomReviewUpdateResponse;
import com.jj.swm.domain.studyroom.service.StudyRoomReviewCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomReviewCommandController {

    private final StudyRoomReviewCommandService commandService;

    @PostMapping("/v1/studyroom/{studyRoomId}/review")
    public ApiResponse<StudyRoomReviewCreateResponse> createReview(
            @Valid @RequestBody StudyRoomReviewCreateRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            Principal principal
    ) {
        StudyRoomReviewCreateResponse response = commandService.createReview(
                request,
                studyRoomId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(response);
    }

    @PatchMapping("/v1/studyroom/{studyRoomId}/review/{studyRoomReviewId}")
    public ApiResponse<StudyRoomReviewUpdateResponse> updateReview(
            @Valid @RequestBody StudyRoomReviewUpdateRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            @PathVariable("studyRoomReviewId") Long studyRoomReviewId,
            Principal principal
    ) {
        StudyRoomReviewUpdateResponse response = commandService.updateReview(
                request,
                studyRoomId,
                studyRoomReviewId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(response);
    }

    @PostMapping("/v1/studyroom/review/{studyRoomReviewId}/reply")
    public ApiResponse<StudyRoomReviewReplyCreateResponse> createReviewReply(
            @Valid @RequestBody StudyRoomReviewReplyCreateRequest request,
            @PathVariable("studyRoomReviewId") Long studyRoomReviewId,
            Principal principal
    ) {
        StudyRoomReviewReplyCreateResponse response = commandService.createReviewReply(
                request,
                studyRoomReviewId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(response);
    }

    @PatchMapping("/v1/studyroom/review/reply/{studyRoomReviewReplyId}")
    public ApiResponse<Void> updateReviewReply(
            @Valid @RequestBody StudyRoomReviewReplyUpdateRequest request,
            @PathVariable("studyRoomReviewReplyId") Long studyRoomReviewReplyId,
            Principal principal
    ) {
        commandService.updateReviewReply(
                request,
                studyRoomReviewReplyId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(null);
    }
}
