package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.StudyBookmarkInfo;
import com.jj.swm.domain.study.dto.StudyInquiryCondition;
import com.jj.swm.domain.study.dto.response.StudyInquiryResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.repository.StudyBookmarkRepository;
import com.jj.swm.domain.study.repository.StudyRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
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
            int pageSize,
            StudyInquiryCondition inquiryCondition
    ) {
        List<Study> studies = studyRepository.findAllWithUserAndTags(pageSize + 1, inquiryCondition);

        if (studies.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "study not found");
        }

        boolean hasNext = studies.size() > pageSize;

        List<Study> pagedStudies = hasNext ? studies.subList(0, pageSize) : studies;

        List<Long> studyIds = pagedStudies.stream()
                .map(Study::getId)
                .toList();

        Map<Long, Long> bookmarkIdByStudyId = userId != null
                ? studyBookmarkRepository.findAllByUserIdAndStudyIds(userId, studyIds)
                    .stream()
                    .collect(Collectors.toMap(StudyBookmarkInfo::getId, StudyBookmarkInfo::getStudyId))
                : Collections.emptyMap();

        List<StudyInquiryResponse> inquiryResponses = pagedStudies.stream()
                .map(study -> StudyInquiryResponse.of(study, bookmarkIdByStudyId.getOrDefault(study.getId(), null)))
                .toList();

        return PageResponse.of(inquiryResponses, hasNext);
    }
}
