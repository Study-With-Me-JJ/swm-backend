package com.jj.swm.domain.external.studyroom.service;

import com.jj.swm.domain.common.KoreaRegion;
import com.jj.swm.domain.external.studyroom.dto.response.ExternalStudyRoomOutput;
import com.jj.swm.domain.external.studyroom.dto.response.GetExternalStudyRoomsResponse;
import com.jj.swm.domain.external.studyroom.entity.ExternalStudyRoom;
import com.jj.swm.domain.external.studyroom.repository.ExternalStudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalStudyRoomService {
    private final ExternalStudyRoomRepository externalStudyRoomRepository;

    public GetExternalStudyRoomsResponse getExternalStudyRooms(Pageable pageable, KoreaRegion koreaRegion) {
        Page<ExternalStudyRoom> externalStudyRooms;
        if(koreaRegion != null) {
            externalStudyRooms = externalStudyRoomRepository.findAllByKoreaRegion(pageable, koreaRegion);
        }
        else {
            externalStudyRooms = externalStudyRoomRepository.findAll(pageable);
        }
        return new GetExternalStudyRoomsResponse(externalStudyRooms.map(ExternalStudyRoomOutput::from));
    }
}
