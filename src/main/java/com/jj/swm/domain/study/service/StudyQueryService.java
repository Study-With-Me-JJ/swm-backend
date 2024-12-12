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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${study.page.size}")
    private int studyPageSize;

    @Value("${study.comment.page.size}")
    private int commentPageSize;

    public PageResponse<StudyInquiryResponse> getList(
            UUID userId,
            StudyInquiryCondition inquiryCondition
    ) {
        List<Study> studies = studyRepository.findAllWithUserAndTags(studyPageSize + 1, inquiryCondition);

        if (studies.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "studies not found");
        }

        boolean hasNext = studies.size() > studyPageSize;

        List<Study> pagedStudies = hasNext ? studies.subList(0, studyPageSize) : studies;

        List<Long> studyIds = pagedStudies.stream()
                .map(Study::getId)
                .toList();

        Map<Long, Long> bookmarkIdByStudyId = userId != null
                ? studyBookmarkRepository.findAllByUserIdAndStudyIds(userId, studyIds)
                    .stream()
                    .collect(Collectors.toMap(StudyBookmarkInfo::getId, StudyBookmarkInfo::getStudyId))
                : Collections.emptyMap();

        List<StudyInquiryResponse> inquiryResponses = pagedStudies.stream()
                .map(study -> StudyInquiryResponse.of(
                        study,
                        bookmarkIdByStudyId.getOrDefault(study.getId(), null),
                        study.getUser().getId().equals(userId)
                ))
                .toList();

        return PageResponse.of(inquiryResponses, hasNext);
    }
}
