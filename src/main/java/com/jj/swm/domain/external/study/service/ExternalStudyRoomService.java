package com.jj.swm.domain.external.study.service;

import com.jj.swm.domain.external.study.dto.response.ExternalStudyRoomOutput;
import com.jj.swm.domain.external.study.dto.response.GetExternalStudyRoomsResponse;
import com.jj.swm.domain.external.study.entity.ExternalStudyRoom;
import com.jj.swm.domain.external.study.repository.ExternalStudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalStudyRoomService {
    private final ExternalStudyRoomRepository externalStudyRoomRepository;

    public GetExternalStudyRoomsResponse getExternalStudies(Pageable pageable) {
        Page<ExternalStudyRoom> externalStudyRooms = externalStudyRoomRepository.findAll(pageable);
        return new GetExternalStudyRoomsResponse(externalStudyRooms.map(ExternalStudyRoomOutput::from));
    }

}
