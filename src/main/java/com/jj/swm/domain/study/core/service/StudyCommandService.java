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
    public void createStudy(CreateStudyRequest request, UUID userId) {
        User user = userRepository.getReferenceById(userId);

        Study study = Study.of(request, user);
        studyRepository.save(study);

        insertTagsIfPresent(request.getTags(), study);

        insertImagesIfPresent(request.getImageUrls(), study);

        recruitmentPositionRepository.batchInsert(request.getCreateRecruitmentPositionRequests(), study);
    }

    @Transactional
    public void updateStudy(
            UpdateStudyRequest request,
            Long studyId,
            UUID userId
    ) {
        Study study = findByIdAndUserIdOrThrow(studyId, userId);

        modifyTags(request.getModifyTagRequest(), study);

        modifyImages(request.getModifyImageRequest(), study);

        study.modify(request);
    }

    @Transactional
    public void updateStudyStatus(
            UpdateStudyStatusRequest request,
            Long studyId,
            UUID userId
    ) {
        Study study = findByIdAndUserIdOrThrow(studyId, userId);
        study.modifyStatus(request);
    }

    @Transactional
    public void deleteStudy(Long studyId, UUID userId) {
        Study study = findByIdAndUserIdOrThrow(studyId, userId);

        deleteStudyAndAssociations(studyId, study);
    }

    @Transactional
    public void deleteStudies(DeleteStudiesRequest request, UUID userId) {
        List<Long> studyIds = request.getStudyIds();
        long numToDelete = studyRepository.countByIdInAndUserId(studyIds, userId);

        if (numToDelete != studyIds.size()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "Some Study Not Found");
        }

        deleteStudiesAndAssociations(studyIds);
    }

    @Transactional
    public CreateStudyBookmarkResponse createStudyBookmark(Long studyId, UUID userId) {
        throwIfAlreadyBookmarked(studyId, userId);

        User user = userRepository.getReferenceById(userId);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));

        StudyBookmark studyBookmark = StudyBookmark.of(study, user);
        studyBookmarkRepository.save(studyBookmark);

        return CreateStudyBookmarkResponse.from(studyBookmark);
    }

    @Transactional
    public void deleteStudyBookmark(Long bookmarkId, UUID userId) {
        StudyBookmark studyBookmark = studyBookmarkRepository.findByIdAndUserId(bookmarkId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study bookmark not found"));

        studyBookmarkRepository.delete(studyBookmark);
    }

    @Transactional
    public void createStudyLike(Long studyId, UUID userId) {
        throwIfAlreadyLiked(studyId, userId);

        User user = userRepository.getReferenceById(userId);

        Study study = findByIdUsingLockOrThrow(studyId);

        StudyLike studyLike = StudyLike.of(study, user);
        studyLikeRepository.save(studyLike);

        study.incrementLikeCount();
    }

    @Transactional
    public void deleteStudyLike(Long studyId, UUID userId) {
        StudyLike studyLike = studyLikeRepository.findByUserIdAndStudyId(studyId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study like not found"));

        Study study = findByIdUsingLockOrThrow(studyId);

        studyLikeRepository.delete(studyLike);

        study.decrementLikeCount();
    }

    private void insertTagsIfPresent(List<String> tags, Study study) {
        if (isListPresent(tags)) {
            studyTagRepository.batchInsert(tags, study);
        }
    }

    private void insertImagesIfPresent(List<String> imageUrls, Study study) {
        if (isListPresent(imageUrls)) {
            studyImageRepository.batchInsert(imageUrls, study);
        }
    }

    private Study findByIdAndUserIdOrThrow(Long studyId, UUID userId) {
        return studyRepository.findByIdAndUserId(studyId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private Study findByIdUsingLockOrThrow(Long studyId) {
        return studyRepository.findByIdUsingLock(studyId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study not found"));
    }

    private void modifyTags(ModifyStudyTagRequest request, Study study) {
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
                studyTagRepository.batchInsert(tagsToAdd, study);

            if (isListNotEmpty(tagIdsToRemove))
                studyTagRepository.deleteAllByIdsAndStudyId(tagIdsToRemove, study.getId());
        }
    }

    private void modifyImages(ModifyStudyImageRequest request, Study study) {
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
                studyImageRepository.batchInsert(imageUrlsToAdd, study);

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

    private void throwIfAlreadyBookmarked(Long studyId, UUID userId) {
        if (studyBookmarkRepository.existsByUserIdAndStudyId(studyId, userId)) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Already Bookmarked");
        }
    }

    private void throwIfAlreadyLiked(Long studyId, UUID userId) {
        if (studyLikeRepository.existsByUserIdAndStudyId(studyId, userId)) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Already Liked");
        }
    }
}
