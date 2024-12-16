package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomReviewReplyResponse;
import com.jj.swm.domain.studyroom.dto.response.UpdateStudyRoomReviewResponse;
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
    public ApiResponse<CreateStudyRoomReviewResponse> createReview(
            @Valid @RequestBody CreateStudyRoomReviewRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            Principal principal
    ) {
        CreateStudyRoomReviewResponse response = commandService.createReview(
                request,
                studyRoomId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(response);
    }

    @PatchMapping("/v1/studyroom/{studyRoomId}/review/{studyRoomReviewId}")
    public ApiResponse<UpdateStudyRoomReviewResponse> updateReview(
            @Valid @RequestBody UpdateStudyRoomReviewRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            @PathVariable("studyRoomReviewId") Long studyRoomReviewId,
            Principal principal
    ) {
        UpdateStudyRoomReviewResponse response = commandService.updateReview(
                request,
                studyRoomId,
                studyRoomReviewId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(response);
    }

    @PostMapping("/v1/studyroom/review/{studyRoomReviewId}/reply")
    public ApiResponse<CreateStudyRoomReviewReplyResponse> createReviewReply(
            @Valid @RequestBody CreateStudyRoomReviewReplyRequest request,
            @PathVariable("studyRoomReviewId") Long studyRoomReviewId,
            Principal principal
    ) {
        CreateStudyRoomReviewReplyResponse response = commandService.createReviewReply(
                request,
                studyRoomReviewId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(response);
    }

    @PatchMapping("/v1/studyroom/review/reply/{studyRoomReviewReplyId}")
    public ApiResponse<Void> updateReviewReply(
            @Valid @RequestBody UpdateStudyRoomReviewReplyRequest request,
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
