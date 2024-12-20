package com.jj.swm.domain.study.controller;

import com.jj.swm.domain.study.dto.request.StudyCreateRequest;
import com.jj.swm.domain.study.dto.request.StudyStatusUpdateRequest;
import com.jj.swm.domain.study.dto.request.StudyUpdateRequest;
import com.jj.swm.domain.study.dto.response.StudyBookmarkCreateResponse;
import com.jj.swm.domain.study.service.StudyCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyCommandController {

    private final StudyCommandService studyCommandService;

    @PostMapping("/v1/study")
    public ApiResponse<Void> createStudy(Principal principal, @Valid @RequestBody StudyCreateRequest createRequest) {
        studyCommandService.create(UUID.fromString(principal.getName()), createRequest);

        return ApiResponse.created(null);
    }

    @PatchMapping("/v1/study/{studyId}")
    public ApiResponse<Void> updateStudy(
            Principal principal,
            @PathVariable("studyId") Long studyId,
            @Valid @RequestBody StudyUpdateRequest updateRequest
    ) {
        studyCommandService.update(
                UUID.fromString(principal.getName()),
                studyId,
                updateRequest);

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/study/{studyId}/status")
    public ApiResponse<Void> updateStudyStatus(
            Principal principal,
            @PathVariable("studyId") Long studyId,
            @Valid @RequestBody StudyStatusUpdateRequest updateRequest
    ) {
        studyCommandService.updateStatus(
                UUID.fromString(principal.getName()),
                studyId,
                updateRequest
        );

        return ApiResponse.ok(null);
    }

    @DeleteMapping("/v1/study/{studyId}")
    public ApiResponse<Void> deleteStudy(Principal principal, @PathVariable("studyId") Long studyId) {
        studyCommandService.delete(UUID.fromString(principal.getName()), studyId);

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/study/{studyId}/bookmark")
    public ApiResponse<StudyBookmarkCreateResponse> bookmarkStudy(
            Principal principal, @PathVariable("studyId") Long studyId
    ) {
        StudyBookmarkCreateResponse createResponse = studyCommandService.bookmarkStudy(
                UUID.fromString(principal.getName()), studyId
        );

        return ApiResponse.created(createResponse);
    }

    @DeleteMapping("/v1/study/bookmark/{bookmarkId}")
    public ApiResponse<Void> unBookmarkStudy(Principal principal, @PathVariable("bookmarkId") Long bookmarkId) {
        studyCommandService.unBookmarkStudy(UUID.fromString(principal.getName()), bookmarkId);

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/study/{studyId}/like")
    public ApiResponse<Void> likeStudy(Principal principal, @PathVariable("studyId") Long studyId) {
        studyCommandService.likeStudy(UUID.fromString(principal.getName()), studyId);

        return ApiResponse.created(null);
    }

    @DeleteMapping("/v1/study/{studyId}/like")
    public ApiResponse<Void> unLikeStudy(Principal principal, @PathVariable("studyId") Long studyId) {
        studyCommandService.unLikeStudy(UUID.fromString(principal.getName()), studyId);

        return ApiResponse.ok(null);
    }
}
