package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomReviewReplyRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomReviewRequest;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomReviewReplyResponse;
import com.jj.swm.domain.studyroom.dto.response.DeleteStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.dto.response.UpdateStudyRoomReviewResponse;
import com.jj.swm.domain.studyroom.service.StudyRoomReviewCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "StudyRoomReview", description = "<b>[스터디 룸 이용후기]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomReviewCommandController {

    private final StudyRoomReviewCommandService commandService;

    @Operation(
            summary = "스터디 룸 이용후기 생성",
            description = "스터디 룸 이용후기를 생성합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "성공"
    )
    @PostMapping("/v1/studyroom/{studyRoomId}/review")
    public ApiResponse<CreateStudyRoomReviewResponse> createReview(
            @Valid @RequestBody CreateStudyRoomReviewRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            Principal principal
    ) {
        CreateStudyRoomReviewResponse response = commandService.createReview(
                request,
                studyRoomId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "스터디 룸 이용후기 수정",
            description = "스터디 룸 이용후기 수정합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
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
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "스터디 룸 이용후기 삭제",
            description = "스터디 룸 이용후기 삭제합니다. 답글도 전체 삭제됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @DeleteMapping("/v1/studyroom/{studyRoomId}/review/{studyRoomReviewId}")
    public ApiResponse<DeleteStudyRoomReviewResponse> deleteReview(
            @PathVariable("studyRoomId") Long studyRoomId,
            @PathVariable("studyRoomReviewId") Long studyRoomReviewId,
            Principal principal
    ) {
        DeleteStudyRoomReviewResponse response = commandService.deleteReview(
                studyRoomId,
                studyRoomReviewId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "스터디 룸 이용후기 답글 생성",
            description = "스터디 룸 이용후기 답글을 생성합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "성공"
    )
    @PostMapping("/v1/studyroom/review/{studyRoomReviewId}/reply")
    public ApiResponse<CreateStudyRoomReviewReplyResponse> createReviewReply(
            @Valid @RequestBody CreateStudyRoomReviewReplyRequest request,
            @PathVariable("studyRoomReviewId") Long studyRoomReviewId,
            Principal principal
    ) {
        CreateStudyRoomReviewReplyResponse response = commandService.createReviewReply(
                request,
                studyRoomReviewId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "스터디 룸 이용후기 답글 수정",
            description = "스터디 룸 이용후기 답글을 수정합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PatchMapping("/v1/studyroom/review/reply/{studyRoomReviewReplyId}")
    public ApiResponse<Void> updateReviewReply(
            @Valid @RequestBody UpdateStudyRoomReviewReplyRequest request,
            @PathVariable("studyRoomReviewReplyId") Long studyRoomReviewReplyId,
            Principal principal
    ) {
        commandService.updateReviewReply(
                request,
                studyRoomReviewReplyId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "스터디 룸 이용후기 답글 삭제",
            description = "스터디 룸 이용후기 답글을 삭제합니다. 답글을 단 본인만 삭제 가능합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @DeleteMapping("/v1/studyroom/review/reply/{studyRoomReviewReplyId}")
    public ApiResponse<Void> deleteReviewReply(
            @PathVariable("studyRoomReviewReplyId") Long studyRoomReviewReplyId,
            Principal principal
    ) {
        commandService.deleteReviewReply(studyRoomReviewReplyId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }
}
