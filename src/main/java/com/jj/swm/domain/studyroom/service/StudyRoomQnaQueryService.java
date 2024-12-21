package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import com.jj.swm.domain.studyroom.repository.StudyRoomQnaRepository;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyRoomQnaQueryService {

    private final StudyRoomQnaRepository qnaRepository;

    @Value("${studyroom.qna.page.size}")
    private int qnaPageSize;

    @Transactional(readOnly = true)
    public PageResponse<GetStudyRoomQnaResponse> getStudyRoomQnas(Long studyRoomId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, qnaPageSize, Sort.by("id").descending());

        Page<StudyRoomQna> pagedQnas
                = qnaRepository.findPagedQnaWithUserByStudyRoomId(studyRoomId, pageable);

        return PageResponse.of(pagedQnas, GetStudyRoomQnaResponse::from);
    }
}
