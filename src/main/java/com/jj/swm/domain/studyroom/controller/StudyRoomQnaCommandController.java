package com.jj.swm.domain.studyroom.controller;

import com.jj.swm.domain.studyroom.dto.request.UpsertStudyRoomQnaRequest;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.dto.response.UpdateStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.service.StudyRoomQnaCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "StudyRoomQna", description = "<b>[스터디 룸 Qna]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomQnaCommandController {

    private final StudyRoomQnaCommandService commandService;

    @Operation(
            summary = "스터디 룸 QnA 생성",
            description = "스터디 룸 QnA를 생성합니다. QnA의 대댓글도 같은 API를 사용합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "성공"
    )
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
                UUID.fromString(principal.getName())
        );

        return ApiResponse.created(response);
    }

    @Operation(
            summary = "스터디 룸 QnA 수정",
            description = "스터디 룸 QnA를 수정합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @PatchMapping("/v1/studyroom/qna/{studyRoomQnaId}")
    public ApiResponse<UpdateStudyRoomQnaResponse> updateQna(
            @Valid @RequestBody UpsertStudyRoomQnaRequest request,
            @PathVariable("studyRoomQnaId") Long studyRoomQnaId,
            Principal principal
    ) {
        UpdateStudyRoomQnaResponse response = commandService.updateQna(
                request,
                studyRoomQnaId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "스터디 룸 QnA 삭제",
            description = "스터디 룸 QnA를 삭제합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @DeleteMapping("/v1/studyroom/qna/{studyRoomQnaId}")
    public ApiResponse<Void> deleteQna(
            @PathVariable("studyRoomQnaId") Long studyRoomQnaId, Principal principal
    ) {
        commandService.deleteQna(studyRoomQnaId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }
}
