package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomCreateRequest;
import com.jj.swm.domain.studyroom.service.StudyRoomCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
