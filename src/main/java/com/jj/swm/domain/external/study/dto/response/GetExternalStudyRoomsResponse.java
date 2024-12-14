package com.jj.swm.domain.external.study.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@Getter
public class GetExternalStudyRoomsResponse {
    Page<ExternalStudyRoomOutput> studyRoomOutputs;
}