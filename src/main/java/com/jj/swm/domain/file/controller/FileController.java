package com.jj.swm.domain.file.controller;

import com.jj.swm.domain.file.dto.response.GetPresignedUrlResponse;
import com.jj.swm.domain.file.service.FileService;
import com.jj.swm.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @Operation(summary = "파일 업로드를 위한 PresignedUrl 가져오기", description = "용량 15MB 제한 및 파일 확장자 제한. jpg ,jpeg, png, gif, bmp, webp, pdf")
    @GetMapping("/presigned-url")
    public ApiResponse<GetPresignedUrlResponse> getPresignedUrl(
            @Parameter(description = "파일명") @RequestParam String fileName,
            @Parameter(description = "파일 크기(byte)") @RequestParam long fileSize
    ) {
        return ApiResponse.ok(fileService.getPresignedUrl(fileName, fileSize));
    }
}
