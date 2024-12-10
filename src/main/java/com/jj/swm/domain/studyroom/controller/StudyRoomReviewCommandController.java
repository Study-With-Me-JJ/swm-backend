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

    @PostMapping("/v1/studyroom/review")
    public ApiResponse<StudyRoomReviewCreateResponse> createReview(
            @Valid @RequestBody StudyRoomReviewCreateRequest request, Principal principal
    ) {
        StudyRoomReviewCreateResponse response =
                commandService.createReview(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(response);
    }

    @PatchMapping("/v1/studyroom/review")
    public ApiResponse<StudyRoomReviewUpdateResponse> updateReview(
            @Valid @RequestBody StudyRoomReviewUpdateRequest request, Principal principal
    ) {
        StudyRoomReviewUpdateResponse response =
            commandService.updateReview(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(response);
    }

    @PostMapping("/v1/studyroom/review/reply")
    public ApiResponse<StudyRoomReviewReplyCreateResponse> createReviewReply(
            @Valid @RequestBody StudyRoomReviewReplyCreateRequest request, Principal principal
    ) {
        StudyRoomReviewReplyCreateResponse response =
                commandService.createReviewReply(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(response);
    }

    @PatchMapping("/v1/studyroom/review/reply")
    public ApiResponse<Void> updateReviewReply(
            @Valid @RequestBody StudyRoomReviewReplyUpdateRequest request, Principal principal
    ) {
        commandService.updateReviewReply(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }
}
