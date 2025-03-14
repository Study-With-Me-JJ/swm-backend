package com.jj.swm.domain.study.core.service;

import com.jj.swm.domain.study.comment.repository.CommentRepository;
import com.jj.swm.domain.study.constants.StudyConstants;
import com.jj.swm.domain.study.core.dto.request.*;
import com.jj.swm.domain.study.core.dto.response.CreateStudyBookmarkResponse;
import com.jj.swm.domain.study.core.entity.Study;
import com.jj.swm.domain.study.core.entity.StudyBookmark;
import com.jj.swm.domain.study.core.entity.StudyLike;
import com.jj.swm.domain.study.core.repository.*;
import com.jj.swm.domain.study.recruitmentposition.repository.RecruitmentPositionRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jj.swm.global.common.util.ListCheckUtils.isListNotEmpty;
import static com.jj.swm.global.common.util.ListCheckUtils.isListPresent;

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
    public void createStudy(UUID userId, CreateStudyRequest request) {
        User user = userRepository.getReferenceById(userId);

        Study study = Study.of(user, request);
        studyRepository.save(study);

        insertTagsIfPresent(study, request.getTags());

        insertImagesIfPresent(study, request.getImageUrls());

        recruitmentPositionRepository.batchInsert(study, request.getCreateRecruitmentPositionRequests());
    }

    @Transactional
    public void updateStudy(
            UUID userId,
            Long studyId,
            UpdateStudyRequest request
    ) {
        Study study = findByIdAndUserIdOrThrow(userId, studyId);

        modifyTags(study, request.getModifyTagRequest());

        modifyImages(study, request.getModifyImageRequest());

        study.modify(request);
    }

    @Transactional
    public void updateStudyStatus(
            UUID userId,
            Long studyId,
            UpdateStudyStatusRequest request
    ) {
        Study study = findByIdAndUserIdOrThrow(userId, studyId);
        study.modifyStatus(request);
    }

    @Transactional
    public void deleteStudy(UUID userId, Long studyId) {
        Study study = findByIdAndUserIdOrThrow(userId, studyId);

        deleteStudyAndAssociations(studyId, study);
    }

    @Transactional
    public void deleteStudies(UUID userId, DeleteStudiesRequest request) {
        List<Long> studyIds = request.getStudyIds();
        long numToDelete = studyRepository.countByIdInAndUserId(studyIds, userId);

        if (numToDelete != studyIds.size()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "Some Study Not Found");
        }

        deleteStudiesAndAssociations(studyIds);
    }

    @Transactional
    public CreateStudyBookmarkResponse createStudyBookmark(UUID userId, Long studyId) {
        throwIfAlreadyBookmarked(userId, studyId);

        User user = userRepository.getReferenceById(userId);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        StudyBookmark studyBookmark = StudyBookmark.of(study, user);
        studyBookmarkRepository.save(studyBookmark);

        return CreateStudyBookmarkResponse.from(studyBookmark);
    }

    @Transactional
    public void deleteStudyBookmark(UUID userId, Long bookmarkId) {
        StudyBookmark studyBookmark = studyBookmarkRepository.findByIdAndUserId(bookmarkId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study bookmark not found"));

        studyBookmarkRepository.delete(studyBookmark);
    }

    @Transactional
    public void createStudyLike(UUID userId, Long studyId) {
        throwIfAlreadyLiked(userId, studyId);

        User user = userRepository.getReferenceById(userId);

        Study study = findByIdUsingPessimisticLockOrThrow(studyId);

        StudyLike studyLike = StudyLike.of(user, study);
        studyLikeRepository.save(studyLike);

        study.incrementLikeCount();
    }

    @Transactional
    public void deleteStudyLike(UUID userId, Long studyId) {
        StudyLike studyLike = studyLikeRepository.findByUserIdAndStudyId(userId, studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study like not found"));

        Study study = findByIdUsingPessimisticLockOrThrow(studyId);

        studyLikeRepository.delete(studyLike);

        study.decrementLikeCount();
    }

    private void insertTagsIfPresent(Study study, List<String> tags) {
        if (isListPresent(tags)) {
            studyTagRepository.batchInsert(study, tags);
        }
    }

    private void insertImagesIfPresent(Study study, List<String> imageUrls) {
        if (isListPresent(imageUrls)) {
            studyImageRepository.batchInsert(study, imageUrls);
        }
    }

    private Study findByIdAndUserIdOrThrow(UUID userId, Long studyId) {
        return studyRepository.findByIdAndUserId(studyId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private Study findByIdUsingPessimisticLockOrThrow(Long studyId) {
        return studyRepository.findByIdUsingPessimisticLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private void modifyTags(Study study, ModifyStudyTagRequest request) {
        if (request != null) {
            List<String> tagsToAdd = Optional.ofNullable(request.getTagsToAdd())
                    .orElse(Collections.emptyList());
            List<Long> tagIdsToRemove = Optional.ofNullable(request.getTagIdsToRemove())
                    .orElse(Collections.emptyList());

            int oldTagSize = studyTagRepository.countByStudyId(study.getId());
            int newTagSize = oldTagSize + tagsToAdd.size() - tagIdsToRemove.size();

            if (newTagSize < 0 || newTagSize > StudyConstants.TAG_LIMIT) {
                throw new GlobalException(ErrorCode.NOT_VALID, "Tag Limit Exceeded");
            }

            if (isListNotEmpty(tagsToAdd))
                studyTagRepository.batchInsert(study, tagsToAdd);

            if (isListNotEmpty(tagIdsToRemove))
                studyTagRepository.deleteAllByIdsAndStudyId(tagIdsToRemove, study.getId());
        }
    }

    private void modifyImages(Study study, ModifyStudyImageRequest request) {
        if (request != null) {
            List<String> imageUrlsToAdd = Optional.ofNullable(request.getImageUrlsToAdd())
                    .orElse(Collections.emptyList());
            List<Long> imageIdsToRemove = Optional.ofNullable(request.getImageIdsToRemove())
                    .orElse(Collections.emptyList());

            int oldTagSize = studyTagRepository.countByStudyId(study.getId());
            int newImageSize = oldTagSize + imageUrlsToAdd.size() - imageIdsToRemove.size();

            if (newImageSize < 0 || newImageSize > StudyConstants.IMAGE_LIMIT) {
                throw new GlobalException(ErrorCode.NOT_VALID, "Image Limit Exceeded");
            }

            if (isListNotEmpty(imageUrlsToAdd))
                studyImageRepository.batchInsert(study, imageUrlsToAdd);

            if (isListNotEmpty(imageIdsToRemove)) {
                studyImageRepository.deleteAllByIdsAndStudyId(imageIdsToRemove, study.getId());
            }
        }
    }

    private void deleteStudyAndAssociations(Long studyId, Study study) {
        studyTagRepository.deleteAllByStudyId(studyId);
        studyImageRepository.deleteAllByStudyId(studyId);
        recruitmentPositionRepository.deleteAllByStudyId(studyId);
        studyLikeRepository.deleteAllByStudyId(studyId);
        commentRepository.deleteAllByStudyId(studyId);
        studyBookmarkRepository.deleteAllByStudyId(studyId);
        studyRepository.delete(study);
    }

    public void deleteStudiesAndAssociations(List<Long> studyIds) {
        studyTagRepository.deleteAllByStudyIds(studyIds);
        studyImageRepository.deleteAllByStudyIds(studyIds);
        recruitmentPositionRepository.deleteAllByStudyIds(studyIds);
        studyLikeRepository.deleteAllByStudyIds(studyIds);
        commentRepository.deleteAllByStudyIds(studyIds);
        studyBookmarkRepository.deleteAllByStudyIds(studyIds);
        studyRepository.deleteAllByStudyIds(studyIds);
    }

    private void throwIfAlreadyBookmarked(UUID userId, Long studyId) {
        if (studyBookmarkRepository.existsByUserIdAndStudyId(userId, studyId)) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Already Bookmarked");
        }
    }

    private void throwIfAlreadyLiked(UUID userId, Long studyId) {
        if(studyLikeRepository.existsByUserIdAndStudyId(userId, studyId)){
            throw new GlobalException(ErrorCode.NOT_VALID, "Already Liked");
        }
    }
}
