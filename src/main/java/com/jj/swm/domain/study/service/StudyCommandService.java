package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.*;
import com.jj.swm.domain.study.dto.response.StudyBookmarkCreateResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyBookmark;
import com.jj.swm.domain.study.entity.StudyLike;
import com.jj.swm.domain.study.entity.StudyRecruitmentPosition;
import com.jj.swm.domain.study.repository.*;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudyCommandService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyTagRepository studyTagRepository;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyImageRepository studyImageRepository;
    private final StudyBookmarkRepository studyBookmarkRepository;
    private final StudyRecruitmentPositionRepository studyRecruitmentPositionRepository;

    @Transactional
    public void create(UUID userId, StudyCreateRequest createRequest) {
        User user = getUser(userId);

        Study study = Study.of(user, createRequest);
        studyRepository.save(study);

        if (createRequest.getTags() != null && !createRequest.getTags().isEmpty()) {
            studyTagRepository.batchInsert(study, createRequest.getTags());
        }

        if (createRequest.getImageUrls() != null && !createRequest.getImageUrls().isEmpty()) {
            studyImageRepository.batchInsert(study, createRequest.getImageUrls());
        }

        studyRecruitmentPositionRepository.batchInsert(study, createRequest.getRecruitPositionsCreateRequests());
    }

    @Transactional
    public void update(UUID userId, Long studyId, StudyUpdateRequest updateRequest) {
        Study study = studyRepository.findByIdAndUserId(studyId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        study.modify(updateRequest);

        modifyTags(study, updateRequest.getTagModifyRequest());
        modifyImages(study, updateRequest.getImageModifyRequest());
        modifyRecruitPosition(study, updateRequest.getRecruitPositionModifyRequest());
    }

    private void modifyTags(Study study, StudyTagModifyRequest modifyRequest) {
        if (modifyRequest != null) {
            List<String> addTags = modifyRequest.getNewTags();
            if (addTags != null && !addTags.isEmpty()) {
                studyTagRepository.batchInsert(study, addTags);
            }

            List<Long> deleteTagIds = modifyRequest.getDeletedTagIds();
            if (deleteTagIds != null && !deleteTagIds.isEmpty()) {
                studyTagRepository.deleteAllByIdInAndStudyId(deleteTagIds, study.getId());
            }
        }
    }

    private void modifyImages(Study study, StudyImageModifyRequest modifyRequest) {
        if (modifyRequest != null) {
            List<String> addImageUrls = modifyRequest.getNewImageUrls();
            if (addImageUrls != null && !addImageUrls.isEmpty()) {
                studyImageRepository.batchInsert(study, addImageUrls);
            }

            List<Long> deleteImageIds = modifyRequest.getDeletedImageIds();
            if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
                studyImageRepository.deleteAllByIdInAndStudyId(deleteImageIds, study.getId());
            }
        }
    }

    private void modifyRecruitPosition(Study study, StudyRecruitPositionModifyRequest modifyRequest) {
        if (modifyRequest != null) {
            List<StudyRecruitPositionsCreateRequest> createRequests = modifyRequest.getNewRecruitPositions();
            if (createRequests != null && !createRequests.isEmpty()) {
                studyRecruitmentPositionRepository.batchInsert(study, createRequests);
            }

            List<StudyRecruitPositionUpdateRequest> recruitPositionsUpdateRequests =
                    modifyRequest.getUpdatedRecruitPositions();
            if (recruitPositionsUpdateRequests != null && !recruitPositionsUpdateRequests.isEmpty()) {
                updateRecruitPositions(study, recruitPositionsUpdateRequests);
            }

            List<Long> deleteRecruitPositionIds = modifyRequest.getDeletedRecruitPositionIds();
            if (deleteRecruitPositionIds != null && !deleteRecruitPositionIds.isEmpty()) {
                studyRecruitmentPositionRepository.deleteAllByIdInAndStudyId(deleteRecruitPositionIds, study.getId());
            }
        }
    }

    private void updateRecruitPositions(Study study, List<StudyRecruitPositionUpdateRequest> updateRequests) {
        Map<Long, StudyRecruitPositionsCreateRequest> createRequestByRecruitPositionId = new HashMap<>();
        List<Long> recruitPositionIds = new ArrayList<>();

        for (StudyRecruitPositionUpdateRequest updateRequest : updateRequests) {
            Long recruitPositionId = updateRequest.getRecruitPositionId();
            createRequestByRecruitPositionId.put(recruitPositionId, updateRequest.getRecruitPositionChanges());
            recruitPositionIds.add(recruitPositionId);
        }

        List<StudyRecruitmentPosition> studyRecruitmentPositions =
                studyRecruitmentPositionRepository.findAllByIdInAndStudy(recruitPositionIds, study);

        if (studyRecruitmentPositions.size() != updateRequests.size()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some recruit position not matching Study.");
        }

        studyRecruitmentPositions.forEach(studyRecruitmentPosition ->
                studyRecruitmentPosition.modify(createRequestByRecruitPositionId.get(studyRecruitmentPosition.getId())));
    }

    @Transactional
    public StudyBookmarkCreateResponse bookmarkStudy(UUID userId, Long studyId) {
        Optional<StudyBookmark> optionalStudyBookmark = studyBookmarkRepository.findByUserIdAndStudyId(userId, studyId);
        if (optionalStudyBookmark.isPresent()) {
            return StudyBookmarkCreateResponse.from(optionalStudyBookmark.get());
        }

        User user = getUser(userId);

        Study study = getStudy(studyId);

        StudyBookmark studyBookmark = StudyBookmark.of(study, user);
        studyBookmarkRepository.save(studyBookmark);

        return StudyBookmarkCreateResponse.from(studyBookmark);
    }

    @Transactional
    public void unBookmarkStudy(UUID userId, Long bookmarkId) {
        studyBookmarkRepository.deleteByIdAndUserId(bookmarkId, userId);
    }

    @Transactional
    public void likeStudy(UUID userId, Long studyId) {
        Optional<StudyLike> optionalStudyLike = studyLikeRepository.findByUserIdAndStudyId(userId, studyId);
        if (optionalStudyLike.isPresent()) {
            return;
        }

        User user = getUser(userId);

        Study study = getStudyPessimisticLock(studyId);

        StudyLike studyLike = StudyLike.of(user, study);
        studyLikeRepository.save(studyLike);

        study.incrementLikeCount();
    }

    @Transactional
    public void unLikeStudy(UUID userId, Long studyId) {
        Study study = getStudyPessimisticLock(studyId);

        StudyLike studyLike = studyLikeRepository.findByUserIdAndStudyId(userId, studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study like not found"));

        studyLikeRepository.delete(studyLike);

        study.decrementLikeCount();
    }

    private User getUser(UUID userId) {
        return userRepository.getReferenceById(userId);
    }

    private Study getStudy(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private Study getStudyPessimisticLock(Long studyId) {
        return studyRepository.findByIdWithPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }
}
