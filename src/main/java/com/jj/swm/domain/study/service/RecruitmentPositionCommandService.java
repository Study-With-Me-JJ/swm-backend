package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.RecruitPositionUpsertRequest;
import com.jj.swm.domain.study.dto.response.RecruitmentPositionApplyResponse;
import com.jj.swm.domain.study.dto.response.RecruitmentPositionCreateResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyParticipant;
import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.repository.StudyParticipantRepository;
import com.jj.swm.domain.study.repository.StudyRecruitmentPositionRepository;
import com.jj.swm.domain.study.repository.StudyRepository;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecruitmentPositionCommandService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository participantRepository;
    private final StudyRecruitmentPositionRepository recruitmentPositionRepository;

    @Transactional
    public RecruitmentPositionCreateResponse create(
            UUID userId,
            Long studyId,
            RecruitPositionUpsertRequest createRequest
    ) {
        Study study = studyRepository.findByIdAndUserId(studyId, userId)
                .orElseThrow(() -> new IllegalArgumentException("study not found"));

        StudyRecruitmentPosition recruitmentPosition = StudyRecruitmentPosition.of(study, createRequest);
        recruitmentPositionRepository.save(recruitmentPosition);

        return RecruitmentPositionCreateResponse.from(recruitmentPosition);
    }

    public RecruitmentPositionApplyResponse apply(UUID userId, Long recruitPositionId) {
        User user = userRepository.getReferenceById(userId);

        StudyRecruitmentPosition recruitmentPosition = recruitmentPositionRepository.findById(recruitPositionId)
                .orElseThrow(() -> new IllegalArgumentException("recruitment position not found"));

        Optional<StudyParticipant> optionalStudyParticipant =
                participantRepository.findByUserIdAndStudyRecruitmentPositionId(userId, recruitPositionId);

        if (optionalStudyParticipant.isPresent()) {
            return RecruitmentPositionApplyResponse.from(optionalStudyParticipant.get());
        }

        StudyParticipant participant = StudyParticipant.of(recruitmentPosition, user);
        participantRepository.save(participant);

        return RecruitmentPositionApplyResponse.from(participant);
    }

    @Transactional
    public void update(
            UUID userId,
            Long recruitPositionId,
            RecruitPositionUpsertRequest updateRequest) {
        StudyRecruitmentPosition recruitmentPosition = recruitmentPositionRepository.findByIdAndUserId(
                recruitPositionId, userId
        ).orElseThrow(() -> new IllegalArgumentException("recruitment position not found"));

        recruitmentPosition.modify(updateRequest);
    }
}
