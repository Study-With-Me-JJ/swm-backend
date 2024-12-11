package com.jj.swm.domain.external.study.service;

import com.jj.swm.domain.external.study.dto.response.ExternalStudyDto;
import com.jj.swm.domain.external.study.dto.response.GetExternalStudiesResponse;
import com.jj.swm.domain.external.study.repository.ExternalStudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalStudyService {
    private final ExternalStudyRepository externalStudyRepository;

    public GetExternalStudiesResponse getExternalStudies(Pageable pageable) {
        return new GetExternalStudiesResponse(externalStudyRepository.findAllExternalStudiesOrderByDeadlineDate(pageable).map(ExternalStudyDto::of));
    }
}
