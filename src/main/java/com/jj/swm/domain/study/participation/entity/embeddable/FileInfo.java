package com.jj.swm.domain.study.participation.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileInfo {

    @NotBlank
    @Size(max = 300)
    @Column(name = "file_url", length = 300)
    private String fileUrl;

    @NotBlank
    @Size(max = 255)
    @Column(name = "file_name")
    private String fileName;
}
