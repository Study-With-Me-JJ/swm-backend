package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomCreateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomDeleteRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomUpdateSettingRequest;
import com.jj.swm.domain.studyroom.dto.request.update.dayoff.StudyRoomDayOffModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.option.StudyRoomOptionInfoModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.reservationType.StudyRoomReservationTypeModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.tag.StudyRoomTagModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.type.StudyRoomTypeInfoModifyRequest;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomLikeCreateResponse;
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
            @Valid @RequestBody StudyRoomCreateRequest request, Principal principal
    ) {
        commandService.create(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.created(null);
    }

    @PatchMapping("/v1/studyroom")
    public ApiResponse<Void> update(
            @Valid @RequestBody StudyRoomUpdateRequest request, Principal principal
    ) {
        commandService.update(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/studyroom/settings")
    public ApiResponse<Void> updateSettings(
            @Valid @RequestBody StudyRoomUpdateSettingRequest request, Principal principal
    ) {
        commandService.updateSettings(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @DeleteMapping("/v1/studyroom")
    public ApiResponse<Void> delete(
            @Valid @RequestBody StudyRoomDeleteRequest request, Principal principal
    ) {
        commandService.delete(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @PostMapping("/v1/studyroom/{studyRoomId}/like")
    public ApiResponse<StudyRoomLikeCreateResponse> createLike(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        StudyRoomLikeCreateResponse response
                = commandService.createLike(studyRoomId, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.created(response);
    }

    @DeleteMapping("/v1/studyroom/like/{studyRoomId}")
    public ApiResponse<Void> deleteLike(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        commandService.deleteLike(studyRoomId, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }
}
