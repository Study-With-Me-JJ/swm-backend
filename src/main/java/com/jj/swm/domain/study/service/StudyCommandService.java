package com.jj.swm.domain.study.service;

import com.jj.swm.domain.study.dto.request.*;
import com.jj.swm.domain.study.dto.response.StudyBookmarkCreateResponse;
import com.jj.swm.domain.study.entity.Study;
import com.jj.swm.domain.study.entity.StudyBookmark;
import com.jj.swm.domain.study.entity.StudyLike;
import com.jj.swm.domain.study.repository.*;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyCommandService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final CommentRepository commentRepository;
    private final StudyTagRepository studyTagRepository;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyImageRepository studyImageRepository;
    private final StudyBookmarkRepository studyBookmarkRepository;
    private final RecruitmentPositionRepository recruitmentPositionRepository;

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

        recruitmentPositionRepository.batchInsert(study, createRequest.getRecruitPositionCreateRequests());
    }

    @Transactional
    public void update(UUID userId, Long studyId, StudyUpdateRequest updateRequest) {
        Study study = getStudy(userId, studyId);
        study.modify(updateRequest);

        modifyTags(study, updateRequest.getTagModifyRequest());
        modifyImages(study, updateRequest.getImageModifyRequest());
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

    @Transactional
    public void updateStatus(
            UUID userId,
            Long studyId,
            StudyStatusUpdateRequest updateRequest
    ) {
        Study study = getStudy(userId, studyId);
        study.modifyStatus(updateRequest);
    }

    @Transactional
    public void delete(UUID userId, Long studyId) {
        Study study = getStudy(userId, studyId);

        deleteStudy(studyId, study);
    }

    @Transactional
    public StudyBookmarkCreateResponse bookmarkStudy(UUID userId, Long studyId) {
        Optional<StudyBookmark> optionalStudyBookmark = studyBookmarkRepository.findByUserIdAndStudyId(userId, studyId);
        if (optionalStudyBookmark.isPresent()) {
            return StudyBookmarkCreateResponse.from(optionalStudyBookmark.get());
        }

        User user = getUser(userId);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

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

    private Study getStudy(UUID userId, Long studyId) {
        return studyRepository.findByIdAndUserId(studyId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private Study getStudyPessimisticLock(Long studyId) {
        return studyRepository.findByIdWithUserUsingPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private void deleteStudy(Long studyId, Study study) {
        studyTagRepository.deleteAllByStudyId(studyId);
        studyImageRepository.deleteAllByStudyId(studyId);
        recruitmentPositionRepository.deleteAllByStudyId(studyId);
        studyLikeRepository.deleteAllByStudyId(studyId);
        commentRepository.deleteAllByStudyId(studyId);
        studyBookmarkRepository.deleteAllByStudyId(studyId);
        studyRepository.delete(study);
    }
}
