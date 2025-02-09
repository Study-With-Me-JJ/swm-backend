package com.jj.swm.domain.file.service;

import com.jj.swm.domain.file.dto.response.GetPresignedUrlResponse;
import com.jj.swm.infra.s3.S3ClientWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final S3ClientWrapper s3ClientWrapper;

    public GetPresignedUrlResponse getPresignedUrl() {
        return new GetPresignedUrlResponse(s3ClientWrapper.getPresignedURL(true, UUID.randomUUID() + "/o"));
    }
}
