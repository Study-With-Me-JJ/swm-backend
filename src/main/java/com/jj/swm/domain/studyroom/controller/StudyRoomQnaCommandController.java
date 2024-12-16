package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.UpsertStudyRoomQnaRequest;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.dto.response.UpdateStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.service.StudyRoomQnaCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomQnaCommandController {

    private final StudyRoomQnaCommandService commandService;

    @PostMapping({"/v1/studyroom/{studyRoomId}/qna", "/v1/studyroom/{studyRoomId}/qna/{parentId}"})
    public ApiResponse<CreateStudyRoomQnaResponse> createQna(
            @Valid @RequestBody UpsertStudyRoomQnaRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            @PathVariable(value = "parentId", required = false) Long parentId,
            Principal principal
    ) {
        CreateStudyRoomQnaResponse response = commandService.createQna(
                request,
                studyRoomId,
                parentId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.created(response);
    }

    @PatchMapping("/v1/studyroom/qna/{studyRoomQnaId}")
    public ApiResponse<UpdateStudyRoomQnaResponse> updateQna(
            @Valid @RequestBody UpsertStudyRoomQnaRequest request,
            @PathVariable("studyRoomQnaId") Long studyRoomQnaId,
            Principal principal
    ) {
        UpdateStudyRoomQnaResponse response = commandService.updateQna(
                request,
                studyRoomQnaId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.ok(response);
    }

    @DeleteMapping("/v1/studyroom/qna/{studyRoomQnaId}")
    public ApiResponse<Void> deleteQna(
            @PathVariable("studyRoomQnaId") Long studyRoomQnaId, Principal principal
    ) {
        commandService.deleteQna(studyRoomQnaId, UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9"));

        return ApiResponse.ok(null);
    }
}
