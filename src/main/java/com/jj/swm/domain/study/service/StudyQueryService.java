package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.StudyBookmarkInfo;
import com.jj.swm.domain.study.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.dto.response.StudyInquiryResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.repository.StudyBookmarkRepository;
import com.jj.swm.domain.study.repository.StudyRepository;
import com.jj.swm.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyQueryService {

    private final StudyRepository studyRepository;
    private final StudyBookmarkRepository studyBookmarkRepository;

    public PageResponse<StudyInquiryResponse> getList(
            UUID userId,
            int pageNo,
            int pageSize,
            StudyInquiryCondition inquiryCondition
    ) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        List<Study> studies = studyRepository.findAllWithUserAndTags(pageable, inquiryCondition);

        List<Long> studyIds = studies.stream()
                .map(Study::getId)
                .toList();

        Map<Long, Long> bookmarkIdByStudyId = userId != null
                ? studyBookmarkRepository.findAllByUserIdAndStudyIds(userId, studyIds)
                    .stream()
                    .collect(Collectors.toMap(StudyBookmarkInfo::getId, StudyBookmarkInfo::getStudyId))
                : Collections.emptyMap();

        List<StudyInquiryResponse> inquiryResponses = studies.stream()
                .map(study -> StudyInquiryResponse.of(study, bookmarkIdByStudyId.getOrDefault(study.getId(), null)))
                .toList();

        Long totalStudies = studyRepository.countTotal(inquiryCondition);

        return PageResponse.of(new PageImpl<>(inquiryResponses, pageable, totalStudies));
    }
}
