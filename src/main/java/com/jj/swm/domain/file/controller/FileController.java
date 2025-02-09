package com.jj.swm.domain.file.controller;

import com.jj.swm.domain.file.dto.response.GetPresignedUrlResponse;
import com.jj.swm.domain.file.service.FileService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @Operation(description = "파일 업로드를 위한 PresignedUrl 가져오기")
    @GetMapping("/presigned-url")
    public ApiResponse<GetPresignedUrlResponse> getPresignedUrl() {
        return ApiResponse.ok(fileService.getPresignedUrl());
    }
}
