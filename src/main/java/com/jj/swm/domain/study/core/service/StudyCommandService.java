package com.jj.swm.domain.study.core.service;

import com.google.common.collect.Lists;
import com.jj.swm.domain.study.comment.repository.StudyCommentRepository;
import com.jj.swm.domain.study.core.constants.StudyConstants;
import com.jj.swm.domain.study.core.dto.request.*;
import com.jj.swm.domain.study.core.dto.response.CreateStudyBookmarkResponse;
import com.jj.swm.domain.study.core.dto.response.GetRecruitmentPositionResponse;
import com.jj.swm.domain.study.core.entity.*;
import com.jj.swm.domain.study.core.repository.*;
import com.jj.swm.domain.study.participation.dto.AcceptedStudyParticipationCountInfo;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.common.util.ListCheckUtils;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jj.swm.global.common.util.ListCheckUtils.isListNotEmpty;
import static com.jj.swm.global.common.util.ListCheckUtils.isListPresent;

@Service
@RequiredArgsConstructor
public class StudyCommandService {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyTagRepository studyTagRepository;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyImageRepository studyImageRepository;
    private final StudyCommentRepository studyCommentRepository;
    private final StudyBookmarkRepository studyBookmarkRepository;
    private final StudyParticipationRepository participationRepository;
    private final RecruitmentPositionRepository recruitmentPositionRepository;
    private final StudyParticipationLinkRepository participationLinkRepository;

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

        deleteStudyAndAssociations(study);
    }

    @Transactional
    public void deleteStudies(DeleteStudiesRequest request, UUID userId) {
        List<Long> studyIds = request.getStudyIds();

        validateStudyOwnership(studyIds, userId);

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
        StudyLike studyLike = studyLikeRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "study like not found"));

        Study study = findByIdUsingLockOrThrow(studyId);

        studyLikeRepository.delete(studyLike);

        study.decrementLikeCount();
    }

    @Transactional
    public List<GetRecruitmentPositionResponse> modifyRecruitmentPosition(
            ModifyRecruitmentPositionRequest request,
            Long studyId,
            UUID userId
    ) {
        List<StudyRecruitmentPosition> recruitmentPositions = recruitmentPositionRepository.findByStudyIdAndStudyUserId(
                studyId, userId
        );

        if (recruitmentPositions.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "study can not exist");
        }

        List<CreateRecruitmentPositionRequest> createRequests =
                Optional.ofNullable(request.getCreateRecruitmentPositionRequests())
                        .orElse(Collections.emptyList());
        List<Long> recruitmentPositionIdsToUpdate = Optional.ofNullable(request.getUpdateRecruitmentPositionRequests())
                .orElse(Collections.emptyList())
                .stream()
                .map(UpdateRecruitmentPositionRequest::getRecruitmentPositionId)
                .toList();
        List<Long> recruitmentPositionIdsToRemove = Optional.ofNullable(request.getRecruitmentPositionIdsToRemove())
                .orElse(Collections.emptyList());

        int oldRecruitmentPositionSize = recruitmentPositions.size();
        int removeCount = (int) recruitmentPositions.stream()
                .filter(recruitmentPosition -> recruitmentPositionIdsToRemove.contains(recruitmentPosition.getId()))
                .count();

        if (removeCount != recruitmentPositionIdsToRemove.size()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "some recruitmentPosition not found");
        }

        int newRecruitmentPositionSize = oldRecruitmentPositionSize + createRequests.size() - removeCount;

        if (newRecruitmentPositionSize < 1 || newRecruitmentPositionSize > StudyConstants.RECRUITMENT_POSITION_LIMIT) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "recruitment position limit deviation");
        }

        if (recruitmentPositionIdsToUpdate.stream().anyMatch(recruitmentPositionIdsToRemove::contains)) {
            throw new GlobalException(ErrorCode.NOT_VALID, "recruitmentPositionIdsToRemove contains updateRecruitmentPositionIds");
        } // 대충 invalid input와 같이 오류 메세지 하면 될 거 같은데

        int updateCount = (int) recruitmentPositions.stream()
                .filter(recruitmentPosition -> recruitmentPositionIdsToUpdate.contains(recruitmentPosition.getId()))
                .count();

        if (updateCount != recruitmentPositionIdsToUpdate.size()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "some recruitmentPositions not found");
        }

        if (removeCount > 0) {
            List<Long> participationIds = participationRepository.findIdsByRecruitmentPositionIds(recruitmentPositionIdsToRemove);
            Lists.partition(participationIds, batchSize)
                    .forEach(participationLinkRepository::deleteAllByParticipationIds);
            participationRepository.deleteAllByRecruitmentPositionIds(recruitmentPositionIdsToRemove);
            recruitmentPositionRepository.deleteAllByIds(recruitmentPositionIdsToRemove);
        }

        if (isListNotEmpty(recruitmentPositionIdsToUpdate)) {
            List<AcceptedStudyParticipationCountInfo> acceptedStudyParticipationCountInfos =
                    participationRepository.countByRecruitmentPositionIdsAndAcceptedStatus(recruitmentPositionIdsToUpdate);
            Map<Long, Integer> acceptedCountByRecruitmentPositionId = acceptedStudyParticipationCountInfos.stream()
                    .collect(Collectors.toMap(
                            AcceptedStudyParticipationCountInfo::getRecruitmentPositionId,
                            AcceptedStudyParticipationCountInfo::getAcceptedStudyParticipationCount
                    ));

            Map<Long, StudyRecruitmentPosition> recruitmentPositionByRecruitmentPositionId = recruitmentPositions.stream()
                    .collect(Collectors.toMap(
                            StudyRecruitmentPosition::getId, recruitmentPosition -> recruitmentPosition
                    ));

            for (UpdateRecruitmentPositionRequest updateRequest : request.getUpdateRecruitmentPositionRequests()) {
                if (updateRequest.getHeadcount() <
                        acceptedCountByRecruitmentPositionId.getOrDefault(updateRequest.getRecruitmentPositionId(), 0)) {
                    throw new GlobalException(ErrorCode.NOT_VALID, "accepted count is greater than headcount");
                }
                recruitmentPositionByRecruitmentPositionId.get(updateRequest.getRecruitmentPositionId()).modify(updateRequest);
            }
        }

        if (ListCheckUtils.isListNotEmpty(createRequests)) {
            Study study = studyRepository.getReferenceById(studyId);

            recruitmentPositionRepository.batchInsert(createRequests, study);
        }

        List<Long> notNewRecruitmentPositionId = Stream.concat(
                recruitmentPositionIdsToRemove.stream(), recruitmentPositionIdsToUpdate.stream()
        ).toList();

        List<StudyRecruitmentPosition> newRecruitmentPositions =
                recruitmentPositionRepository.findByIdNotInAndStudyId(notNewRecruitmentPositionId, studyId);

        return newRecruitmentPositions.stream()
                .map(GetRecruitmentPositionResponse::from)
                .toList();
    }

    private void validateStudyOwnership(List<Long> studyIds, UUID userId) {
        long removeCount = studyRepository.countByIdInAndUserId(studyIds, userId);

        if (removeCount != studyIds.size()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "Some Study Not Found");
        }
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
        if (request == null || !(isListPresent(request.getTagsToAdd()) || isListPresent(request.getTagIdsToRemove())))
            return;

        List<String> tagsToAdd = Optional.ofNullable(request.getTagsToAdd())
                .orElse(Collections.emptyList());
        List<Long> tagIdsToRemove = Optional.ofNullable(request.getTagIdsToRemove())
                .orElse(Collections.emptyList());

        List<StudyTag> tags = studyTagRepository.findAllByStudyId(study.getId());

        int oldTagSize = tags.size();
        int removeCount = (int) tags.stream()
                .filter(tag -> tagIdsToRemove.contains(tag.getId()))
                .count();

        if (removeCount != tagIdsToRemove.size()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "some tags not found");
        }

        int newTagSize = oldTagSize + tagsToAdd.size() - removeCount;
        if (newTagSize > StudyConstants.TAG_LIMIT) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Tag Limit Deviation");
        }

        if (removeCount > 0)
            studyTagRepository.deleteAllByIdsAndStudyId(tagIdsToRemove, study.getId());

        if (isListNotEmpty(tagsToAdd)) {
            studyTagRepository.batchInsert(tagsToAdd, study);
        }
    }

    private void modifyImages(ModifyStudyImageRequest request, Study study) {
        if (request == null || !(isListPresent(request.getImageUrlsToAdd()) || isListPresent(request.getImageIdsToRemove())))
            return;

        List<String> imageUrlsToAdd = Optional.ofNullable(request.getImageUrlsToAdd())
                .orElse(Collections.emptyList());
        List<Long> imageIdsToRemove = Optional.ofNullable(request.getImageIdsToRemove())
                .orElse(Collections.emptyList());

        List<StudyImage> images = studyImageRepository.findAllByStudyId(study.getId());

        int oldImageSize = images.size();
        int removeCount = (int) images.stream()
                .filter(image -> imageIdsToRemove.contains(image.getId()))
                .count();

        if (removeCount != imageIdsToRemove.size()) {
            throw new GlobalException(ErrorCode.NOT_FOUND, "some images not found");
        }

        int newImageSize = oldImageSize + imageUrlsToAdd.size() - removeCount;
        if (newImageSize > StudyConstants.IMAGE_LIMIT) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Image Limit Deviation");
        }

        if (removeCount > 0)
            studyImageRepository.deleteAllByIdsAndStudyId(imageIdsToRemove, study.getId());

        if (isListNotEmpty(imageUrlsToAdd))
            studyImageRepository.batchInsert(imageUrlsToAdd, study);
    }

    private void deleteStudyAndAssociations(Study study) {
        studyTagRepository.deleteAllByStudyId(study.getId());
        studyImageRepository.deleteAllByStudyId(study.getId());
        studyLikeRepository.deleteAllByStudyId(study.getId());
        studyCommentRepository.deleteAllByStudyId(study.getId());
        studyBookmarkRepository.deleteAllByStudyId(study.getId());

        List<Long> participationIds = participationRepository.findIdsByStudyId(study.getId());
        Lists.partition(participationIds, batchSize)
                .forEach(participationLinkRepository::deleteAllByParticipationIds);
        participationRepository.deleteAllByStudyId(study.getId());
        recruitmentPositionRepository.deleteAllByStudyId(study.getId());

        studyRepository.delete(study);
    }

    public void deleteStudiesAndAssociations(List<Long> studyIds) {
        studyTagRepository.deleteAllByStudyIds(studyIds);
        studyImageRepository.deleteAllByStudyIds(studyIds);
        studyLikeRepository.deleteAllByStudyIds(studyIds);
        studyCommentRepository.deleteAllByStudyIds(studyIds);
        studyBookmarkRepository.deleteAllByStudyIds(studyIds);

        List<Long> participationIds = participationRepository.findIdsByStudyIds(studyIds);
        Lists.partition(participationIds, batchSize)
                .forEach(participationLinkRepository::deleteAllByParticipationIds);
        participationRepository.deleteAllByStudyIds(studyIds);
        recruitmentPositionRepository.deleteAllByStudyIds(studyIds);

        studyRepository.deleteAllByIds(studyIds);
    }

    private void throwIfAlreadyBookmarked(Long studyId, UUID userId) {
        if (studyBookmarkRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Already Bookmarked");
        }
    }

    private void throwIfAlreadyLiked(Long studyId, UUID userId) {
        if (studyLikeRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Already Liked");
        }
    }
}
