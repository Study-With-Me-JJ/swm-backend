package com.jj.swm.domain.file.service;

import com.jj.swm.domain.file.dto.response.GetPresignedUrlResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import com.jj.swm.infra.s3.S3ClientWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final S3ClientWrapper s3ClientWrapper;

    public GetPresignedUrlResponse getPresignedUrl(String fileName, long fileSize) {
        checkFileExtensionOrThrow(fileName);
        checkFileSizeOrThrow(fileSize);
        return new GetPresignedUrlResponse(s3ClientWrapper.getPresignedURL(true, UUID.randomUUID() + fileName, fileSize));
    }

    // Allow Images, PDF
    private void checkFileExtensionOrThrow(String fileName) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".pdf"};
        String lowerCaseFileName = fileName.toLowerCase();
        for (String ext : allowedExtensions) {
            if (lowerCaseFileName.endsWith(ext)) {
                return;
            }
        }
        throw new GlobalException(ErrorCode.NOT_VALID, "invalid file extension");
    }

    // limit 15MB
    private void checkFileSizeOrThrow(long fileSize) {
        if (fileSize > 15 * 1024 * 1024) {
            throw new GlobalException(ErrorCode.NOT_VALID, "too large file size");
        }
    }
}
