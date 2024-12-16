package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.CreateStudyRoomRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomRequest;
import com.jj.swm.domain.studyroom.dto.request.UpdateStudyRoomSettingRequest;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomBookmarkResponse;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomLikeResponse;
import com.jj.swm.domain.studyroom.service.StudyRoomCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomCommandController {

    private final StudyRoomCommandService commandService;

    @PostMapping("/v1/studyroom")
    public ApiResponse<Void> create(
            @Valid @RequestBody CreateStudyRoomRequest request, Principal principal
    ) {
        commandService.create(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.created(null);
    }

    @PatchMapping("/v1/studyroom/{studyRoomId}")
    public ApiResponse<Void> update(
            @Valid @RequestBody UpdateStudyRoomRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            Principal principal
    ) {
        commandService.update(
                request,
                studyRoomId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/studyroom/settings/{studyRoomId}")
    public ApiResponse<Void> updateSettings(
            @Valid @RequestBody UpdateStudyRoomSettingRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            Principal principal
    ) {
        commandService.updateSettings(
                request,
                studyRoomId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(null);
    }

    @DeleteMapping("/v1/studyroom/{studyRoomId}")
    public ApiResponse<Void> delete(@PathVariable("studyRoomId") Long studyRoomId, Principal principal) {
        commandService.delete(studyRoomId, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/studyroom/{studyRoomId}/like")
    public ApiResponse<CreateStudyRoomLikeResponse> createLike(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        CreateStudyRoomLikeResponse response
                = commandService.createLike(studyRoomId, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.created(response);
    }

    @DeleteMapping("/v1/studyroom/like/{studyRoomId}")
    public ApiResponse<Void> unLike(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        commandService.unLike(studyRoomId, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/studyroom/{studyRoomId}/bookmark")
    public ApiResponse<CreateStudyRoomBookmarkResponse> createBookmark(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        CreateStudyRoomBookmarkResponse response =
                commandService.createBookmark(studyRoomId, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.created(response);
    }

    @DeleteMapping("/v1/studyroom/bookmark/{studyRoomBookmarkId}")
    public ApiResponse<Void> unBookmark(
            @PathVariable("studyRoomBookmarkId") Long studyRoomBookmarkId, Principal principal
    ) {
        commandService.unBookmark(studyRoomBookmarkId, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }
}
