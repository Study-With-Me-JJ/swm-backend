package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomCreateRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomDeleteRequest;
import com.jj.swm.domain.studyroom.dto.request.StudyRoomUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.update.dayoff.StudyRoomDayOffModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.option.StudyRoomOptionInfoModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.reservationType.StudyRoomReservationTypeModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.tag.StudyRoomTagModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.type.StudyRoomTypeInfoModifyRequest;
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

    @PatchMapping("/v1/studyroom/tag")
    public ApiResponse<Void> updateTag(
            @Valid @RequestBody StudyRoomTagModifyRequest request, Principal principal
    ) {
        commandService.updateTag(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/studyroom/dayoff")
    public ApiResponse<Void> updateDayOff(
            @Valid @RequestBody StudyRoomDayOffModifyRequest request, Principal principal
    ) {
        commandService.updateDayOff(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/studyroom/option")
    public ApiResponse<Void> updateOption(
            @Valid @RequestBody StudyRoomOptionInfoModifyRequest request, Principal principal
    ) {
        commandService.updateOption(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/studyroom/type")
    public ApiResponse<Void> updateType(
            @Valid @RequestBody StudyRoomTypeInfoModifyRequest request, Principal principal
    ) {
        commandService.updateType(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @PatchMapping("/v1/studyroom/reservetype")
    public ApiResponse<Void> updateReserveType(
            @Valid @RequestBody StudyRoomReservationTypeModifyRequest request, Principal principal
    ) {
        commandService.updateReserveType(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }

    @DeleteMapping("/v1/studyroom")
    public ApiResponse<Void> delete(
            @Valid @RequestBody StudyRoomDeleteRequest request, Principal principal
    ) {
        commandService.delete(request, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }
}
