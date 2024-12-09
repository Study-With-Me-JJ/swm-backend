package com.jj.swm.domain.study.controller;

import com.jj.swm.domain.study.dto.request.StudyCreateRequest;
import com.jj.swm.domain.study.dto.request.StudyUpdateRequest;
import com.jj.swm.domain.study.dto.response.StudyBookmarkCreateResponse;
import com.jj.swm.domain.study.dto.response.StudyLikeCreateResponse;
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
    public ApiResponse<Void> createStudy(
            Principal principal, @Valid @RequestBody StudyCreateRequest createRequest
    ) {
        studyCommandService.create(
                UUID.fromString(principal.getName()), createRequest
        );

        return ApiResponse.created(null);
    }

    @PutMapping("/v1/study/{studyId}")
    public ApiResponse<Void> updateStudy(
            Principal principal,
            @PathVariable("studyId") Long studyId,
            @Valid @RequestBody StudyUpdateRequest updateRequest
    ) {
        studyCommandService.update(
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"),
                studyId,
                updateRequest);

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/study/{studyId}/bookmark")
    public ApiResponse<StudyBookmarkCreateResponse> createStudyBookmark(
            Principal principal, @PathVariable("studyId") Long studyId
    ) {
        StudyBookmarkCreateResponse createResponse = studyCommandService.createBookmark(
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"), studyId
        );

        return ApiResponse.created(createResponse);
    }

    @DeleteMapping("/v1/study/bookmark/{bookmarkId}")
    public ApiResponse<Void> deleteStudyBookmark(Principal principal, @PathVariable("bookmarkId") Long bookmarkId) {
        studyCommandService.deleteBookmark(UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"), bookmarkId);

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/study/{studyId}/like")
    public ApiResponse<StudyLikeCreateResponse> likeStudy(Principal principal, @PathVariable("studyId") Long studyId) {
        StudyLikeCreateResponse createResponse = studyCommandService.likeStudy(
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"), studyId
        );

        return ApiResponse.created(createResponse);
    }
}
