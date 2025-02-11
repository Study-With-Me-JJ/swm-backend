package com.jj.swm.domain.studyroom.core.controller;

import com.jj.swm.domain.studyroom.core.dto.request.CreateStudyRoomRequest;
import com.jj.swm.domain.studyroom.core.dto.request.UpdateStudyRoomRequest;
import com.jj.swm.domain.studyroom.core.dto.request.UpdateStudyRoomSettingRequest;
import com.jj.swm.domain.studyroom.core.dto.response.CreateStudyRoomBookmarkResponse;
import com.jj.swm.domain.studyroom.core.dto.response.CreateStudyRoomLikeResponse;
import com.jj.swm.domain.studyroom.core.service.StudyRoomCommandService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "StudyRoom", description = "<b>[스터디 룸]</b> API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudyRoomCommandController {

    private final StudyRoomCommandService commandService;

    @Operation(
            summary = "스터디 룸 생성",
            description = "ROOM_ADMIN 권한이 있는 유저가 스터디 룸을 생성을 합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "성공"
    )
    @Secured("ROLE_ROOM_ADMIN")
    @PostMapping("/v1/studyroom")
    public ApiResponse<Void> create(
            @Valid @RequestBody CreateStudyRoomRequest request, Principal principal
    ) {
        commandService.create(request, UUID.fromString(principal.getName()));

        return ApiResponse.created(null);
    }

    @Operation(
            summary = "스터디 룸 수정",
            description = "ROOM_ADMIN 권한이 있는 유저가 스터디 룸 정보를 수정합니다.\n" +
                    "리스트 추가/삭제를 제외한 기존 데이터는 넣어주셔야 합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @Secured("ROLE_ROOM_ADMIN")
    @PatchMapping("/v1/studyroom/{studyRoomId}")
    public ApiResponse<Void> update(
            @Valid @RequestBody UpdateStudyRoomRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            Principal principal
    ) {
        commandService.update(
                request,
                studyRoomId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "스터디 룸 설정 수정",
            description = "ROOM_ADMIN 권한이 있는 유저가 스터디 룸 설정 정보를 수정합니다.\n" +
                    "리스트 추가/삭제를 제외한 기존 데이터는 넣어주셔야 합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @Secured("ROLE_ROOM_ADMIN")
    @PatchMapping("/v1/studyroom/settings/{studyRoomId}")
    public ApiResponse<Void> updateSettings(
            @Valid @RequestBody UpdateStudyRoomSettingRequest request,
            @PathVariable("studyRoomId") Long studyRoomId,
            Principal principal
    ) {
        commandService.updateSettings(
                request,
                studyRoomId,
                UUID.fromString(principal.getName())
        );

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "스터디 룸 삭제",
            description = "ROOM_ADMIN 권한이 있는 유저가 스터디 룸을 삭제합니다. 관련된 모든 정보가 삭제됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @Secured("ROLE_ROOM_ADMIN")
    @DeleteMapping("/v1/studyroom/{studyRoomId}")
    public ApiResponse<Void> delete(@PathVariable("studyRoomId") Long studyRoomId, Principal principal) {
        commandService.delete(studyRoomId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "스터디 룸 좋아요",
            description = "스터디 룸 좋아요 등록입니다. 유저마다 좋아요는 한번만 누를 수 있습니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "성공"
    )
    @PostMapping("/v1/studyroom/{studyRoomId}/like")
    public ApiResponse<CreateStudyRoomLikeResponse> createLike(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        CreateStudyRoomLikeResponse response
                = commandService.createLike(studyRoomId, UUID.fromString(principal.getName()));

        return ApiResponse.created(response);
    }

    @Operation(
            summary = "스터디 룸 좋아요 취소",
            description = "스터디 룸 좋아요 취소입니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @DeleteMapping("/v1/studyroom/like/{studyRoomId}")
    public ApiResponse<Void> unLike(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        commandService.unLike(studyRoomId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }

    @Operation(
            summary = "스터디 룸 북마크",
            description = "스터디 룸 북마크 등록입니다. 유저마다 북마크는 한번만 등록할 수 있습니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "성공"
    )
    @PostMapping("/v1/studyroom/{studyRoomId}/bookmark")
    public ApiResponse<CreateStudyRoomBookmarkResponse> createBookmark(
            @PathVariable("studyRoomId") Long studyRoomId, Principal principal
    ) {
        CreateStudyRoomBookmarkResponse response =
                commandService.createBookmark(studyRoomId, UUID.fromString(principal.getName()));

        return ApiResponse.created(response);
    }

    @Operation(
            summary = "스터디 룸 북마크 취소",
            description = "스터디 룸 북마크 취소입니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "성공"
    )
    @DeleteMapping("/v1/studyroom/bookmark/{studyRoomBookmarkId}")
    public ApiResponse<Void> unBookmark(
            @PathVariable("studyRoomBookmarkId") Long studyRoomBookmarkId, Principal principal
    ) {
        commandService.unBookmark(studyRoomBookmarkId, UUID.fromString(principal.getName()));

        return ApiResponse.ok(null);
    }
}
