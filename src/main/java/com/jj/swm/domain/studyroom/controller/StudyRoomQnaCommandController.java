package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.StudyRoomQnaUpsertRequest;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomQnaCreateResponse;
import com.jj.swm.domain.studyroom.dto.response.StudyRoomQnaUpdateResponse;
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
    public ApiResponse<StudyRoomQnaCreateResponse> createQna(
            @Valid @RequestBody StudyRoomQnaUpsertRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            @PathVariable(value = "parentId", required = false) Long parentId,
            Principal principal
    ) {
        StudyRoomQnaCreateResponse response = commandService.createQna(
                request,
                studyRoomId,
                parentId,
                UUID.fromString("d554b429-366f-4d8e-929d-bb5479623eb9")
        );

        return ApiResponse.created(response);
    }

    @PatchMapping("/v1/studyroom/qna/{studyRoomQnaId}")
    public ApiResponse<StudyRoomQnaUpdateResponse> updateQna(
            @Valid @RequestBody StudyRoomQnaUpsertRequest request,
            @PathVariable("studyRoomQnaId") Long studyRoomQnaId,
            Principal principal
    ) {
        StudyRoomQnaUpdateResponse response = commandService.updateQna(
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
