package com.jj.swm.domain.external.studyroom.controller;

import com.jj.swm.domain.common.KoreaRegion;
import com.jj.swm.domain.external.studyroom.dto.response.GetExternalStudyRoomsResponse;
import com.jj.swm.domain.external.studyroom.service.ExternalStudyRoomService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/studyrooms")
@RequiredArgsConstructor
public class ExternalStudyRoomController {
    private final ExternalStudyRoomService externalStudyRoomService;

    @Operation(summary = "외부 스터디룸 가져오기")
    @GetMapping()
    public ApiResponse<GetExternalStudyRoomsResponse> getExternalStudyRooms(
            @PageableDefault() @ParameterObject Pageable pageable,
            @RequestParam(required = false) KoreaRegion koreaRegion
            ) {
        return ApiResponse.ok(externalStudyRoomService.getExternalStudyRooms(pageable, koreaRegion));
    }
}
