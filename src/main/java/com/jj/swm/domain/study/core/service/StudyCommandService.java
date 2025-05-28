package com.jj.swm.domain.study.core.service;

import com.google.common.collect.Lists;
import com.jj.swm.domain.study.comment.repository.StudyCommentRepository;
import com.jj.swm.domain.study.core.dto.component.CreateRecruitmentPositionInfo;
import com.jj.swm.domain.study.core.dto.request.*;
import com.jj.swm.domain.study.core.dto.request.ModifyRecruitmentPositionRequest.UpdateRecruitmentPositionInfo;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest.ModifyImageInfo;
import com.jj.swm.domain.study.core.dto.request.UpdateStudyRequest.ModifyTagInfo;
import com.jj.swm.domain.study.core.dto.response.CreateRecruitmentPositionResponse;
import com.jj.swm.domain.study.core.dto.response.CreateStudyBookmarkResponse;
import com.jj.swm.domain.study.core.dto.response.CreateStudyLikeResponse;
import com.jj.swm.domain.study.core.entity.*;
import com.jj.swm.domain.study.core.repository.*;
import com.jj.swm.domain.study.participation.repository.StudyParticipationLinkRepository;
import com.jj.swm.domain.study.participation.repository.StudyParticipationRepository;
import com.jj.swm.domain.study.participation.repository.dto.PositionAcceptedParticipationCountInfo;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jj.swm.domain.study.common.EntityModificationValidator.*;
import static com.jj.swm.domain.study.core.constants.StudyConstants.*;
import static com.jj.swm.global.common.enums.ErrorCode.NOT_FOUND;
import static com.jj.swm.global.common.enums.ErrorCode.NOT_VALID;
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
        recruitmentPositionRepository.batchInsert(request.getRecruitmentPositionInfos(), study);
    }

    @Transactional
    public void updateStudy(
            UpdateStudyRequest request,
            Long studyId,
            UUID userId
    ) {
        Study study = findByIdAndUserId(studyId, userId);

        modifyTags(request.getModifyTagInfo(), study);
        modifyImages(request.getModifyImageInfo(), study);

        study.modify(request);
    }

    @Transactional
    public void updateStudyStatus(
            UpdateStudyStatusRequest request,
            Long studyId,
            UUID userId
    ) {
        Study study = findByIdAndUserId(studyId, userId);

        study.modifyStatus(request);
    }

    @Transactional
    public void deleteStudies(DeleteStudyRequest request, UUID userId) {
        List<Long> studyIds = request.getStudyIds();

        validateStudiesOwnership(studyIds, userId);

        deleteStudiesAndAssociations(studyIds);
    }

    @Transactional
    public CreateStudyBookmarkResponse createStudyBookmark(Long studyId, UUID userId) {
        throwIfAlreadyBookmarked(studyId, userId);

        User user = userRepository.getReferenceById(userId);

        Study study = findById(studyId);

        StudyBookmark studyBookmark = StudyBookmark.of(study, user);
        studyBookmarkRepository.save(studyBookmark);

        return CreateStudyBookmarkResponse.from(studyBookmark);
    }

    @Transactional
    public void deleteStudyBookmark(Long bookmarkId, UUID userId) {
        StudyBookmark studyBookmark = studyBookmarkRepository.findByIdAndUserId(bookmarkId, userId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study bookmark not found"));

        studyBookmarkRepository.delete(studyBookmark);
    }

    @Transactional
    public CreateStudyLikeResponse createStudyLike(Long studyId, UUID userId) {
        throwIfAlreadyLiked(studyId, userId);

        User user = userRepository.getReferenceById(userId);

        Study study = findById(studyId);

        StudyLike studyLike = StudyLike.of(study, user);
        studyLikeRepository.save(studyLike);

        studyRepository.incrementLikeCountById(studyId);

        return CreateStudyLikeResponse.from(studyLike);
    }

    @Transactional
    public void deleteStudyLike(Long likeId, UUID userId) {
        StudyLike studyLike = studyLikeRepository.findByIdAndUserId(likeId, userId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study like not found"));

        studyLikeRepository.delete(studyLike);

        studyRepository.decrementLikeCountById(studyLike.getStudy().getId());
    }

    @Transactional
    public List<CreateRecruitmentPositionResponse> modifyRecruitmentPosition(
            ModifyRecruitmentPositionRequest request,
            Long studyId,
            UUID userId
    ) {
        List<StudyRecruitmentPosition> recruitmentPositions = recruitmentPositionRepository.findByStudyIdAndStudyUserId(
                studyId, userId
        );

        validateStudyCanExist(recruitmentPositions);

        List<CreateRecruitmentPositionInfo> infosToAdd = getSafeList(request.getRecruitmentPositionInfosToAdd());
        List<Long> recruitmentPositionIdsToRemove = getSafeList(request.getRecruitmentPositionIdsToRemove());
        List<Long> recruitmentPositionIdsToEdit = getSafeList(request.getRecruitmentPositionInfosToEdit()).stream()
                .map(UpdateRecruitmentPositionInfo::getRecruitmentPositionId)
                .toList();

        validateAllIdsPresent(
                recruitmentPositionIdsToRemove,
                recruitmentPositions,
                StudyRecruitmentPosition::getId,
                "some recruitmentPosition not found"
        );
        validateSizeLimit(
                recruitmentPositions.size() + infosToAdd.size() - recruitmentPositionIdsToRemove.size(),
                1,
                RECRUITMENT_POSITION_LIMIT,
                "recruitment position limit deviation"
        );
        validateNoOverlapBetweenEditAndRemove(recruitmentPositionIdsToEdit, recruitmentPositionIdsToRemove);
        validateAllIdsPresent(
                recruitmentPositionIdsToEdit,
                recruitmentPositions,
                StudyRecruitmentPosition::getId,
                "some recruitmentPosition not found"
        );

        deleteRecruitmentPositionsIfNotEmpty(recruitmentPositionIdsToRemove);
        updateRecruitmentPositionsIfNotEmpty(
                request.getRecruitmentPositionInfosToEdit(),
                recruitmentPositionIdsToEdit,
                recruitmentPositions
        );
        insertRecruitmentPositionsIfNotEmpty(infosToAdd, studyId);

        return recruitmentPositionRepository.findAllByStudyId(studyId).stream()
                .map(CreateRecruitmentPositionResponse::from)
                .toList();
    }

    private void insertRecruitmentPositionsIfNotEmpty(List<CreateRecruitmentPositionInfo> infosToAdd, Long studyId) {
        if (!isListNotEmpty(infosToAdd)) return;

        Study study = studyRepository.getReferenceById(studyId);
        recruitmentPositionRepository.batchInsert(infosToAdd, study);
    }

    private void updateRecruitmentPositionsIfNotEmpty(
            List<UpdateRecruitmentPositionInfo> infosToEdit,
            List<Long> recruitmentPositionIdsToEdit,
            List<StudyRecruitmentPosition> recruitmentPositions
    ) {
        if (!isListNotEmpty(recruitmentPositionIdsToEdit)) return;

        Map<Long, Long> acceptedCountByRecruitmentPositionId =
                participationRepository.countByRecruitmentPositionIdsAndAccepted(recruitmentPositionIdsToEdit).stream()
                        .collect(Collectors.toMap(
                                PositionAcceptedParticipationCountInfo::getRecruitmentPositionId,
                                PositionAcceptedParticipationCountInfo::getAcceptedCount
                        ));

        Map<Long, StudyRecruitmentPosition> recruitmentPositionByRecruitmentPositionId = recruitmentPositions.stream()
                .collect(Collectors.toMap(
                        StudyRecruitmentPosition::getId, recruitmentPosition -> recruitmentPosition
                ));

        for (UpdateRecruitmentPositionInfo infoToEdit : infosToEdit) {
            long acceptedCount =
                    acceptedCountByRecruitmentPositionId.getOrDefault(infoToEdit.getRecruitmentPositionId(), 0L);
            if (infoToEdit.getHeadcount() < acceptedCount)
                throw new GlobalException(NOT_VALID, "accepted count is greater than headcount");

            recruitmentPositionByRecruitmentPositionId.get(infoToEdit.getRecruitmentPositionId())
                    .modify(infoToEdit);
        }
    }

    private void deleteRecruitmentPositionsIfNotEmpty(List<Long> recruitmentPositionIdsToRemove) {
        if (!isListNotEmpty(recruitmentPositionIdsToRemove)) return;

        List<Long> participationIds =
                participationRepository.findIdsByRecruitmentPositionIds(recruitmentPositionIdsToRemove);
        Lists.partition(participationIds, batchSize)
                .forEach(participationLinkRepository::deleteAllByParticipationIds);

        participationRepository.deleteAllByRecruitmentPositionIds(recruitmentPositionIdsToRemove);

        recruitmentPositionRepository.deleteAllByIds(recruitmentPositionIdsToRemove);
    }

    private void validateNoOverlapBetweenEditAndRemove(
            List<Long> recruitmentPositionIdsToEdit, List<Long> recruitmentPositionIdsToRemove
    ) {
        if (recruitmentPositionIdsToEdit.stream().anyMatch(recruitmentPositionIdsToRemove::contains))
            throw new GlobalException(NOT_VALID, "invalid input about edit and remove");
    }

    private void validateStudyCanExist(List<StudyRecruitmentPosition> recruitmentPositions) {
        if (!isListNotEmpty(recruitmentPositions))
            throw new GlobalException(NOT_FOUND, "study can not exist");
    }

    private void validateStudiesOwnership(List<Long> studyIds, UUID userId) {
        long removeCount = studyRepository.countByIdInAndUserId(studyIds, userId);

        if (removeCount != studyIds.size())
            throw new GlobalException(NOT_FOUND, "Some Study Not Found");
    }

    private void insertTagsIfPresent(List<String> tags, Study study) {
        if (isListPresent(tags))
            studyTagRepository.batchInsert(tags, study);
    }

    private void insertImagesIfPresent(List<String> imageUrls, Study study) {
        if (isListPresent(imageUrls))
            studyImageRepository.batchInsert(imageUrls, study);
    }

    private Study findByIdAndUserId(Long studyId, UUID userId) {
        return studyRepository.findByIdAndUserId(studyId, userId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study not found"));
    }

    private Study findById(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new GlobalException(NOT_FOUND, "study not found"));
    }

    private void modifyTags(ModifyTagInfo info, Study study) {
        if (info == null || !(isListPresent(info.getTagsToAdd()) || isListPresent(info.getTagIdsToRemove()))) return;

        List<String> tagsToAdd = getSafeList(info.getTagsToAdd());
        List<Long> tagIdsToRemove = getSafeList(info.getTagIdsToRemove());

        List<StudyTag> tags = studyTagRepository.findAllByStudyId(study.getId());

        validateAllIdsPresent(
                tagIdsToRemove,
                tags,
                StudyTag::getId,
                "some tags not found"
        );
        validateSizeLimit(
                tags.size() + tagsToAdd.size() - tagIdsToRemove.size(),
                TAG_LIMIT,
                "tag limit deviation"
        );

        if (isListNotEmpty(tagIdsToRemove))
            studyTagRepository.deleteAllByIds(tagIdsToRemove);

        if (isListNotEmpty(tagsToAdd))
            studyTagRepository.batchInsert(tagsToAdd, study);
    }

    private void modifyImages(ModifyImageInfo info, Study study) {
        if (info == null || !(isListPresent(info.getImageUrlsToAdd()) || isListPresent(info.getImageIdsToRemove())))
            return;

        List<String> imageUrlsToAdd = getSafeList(info.getImageUrlsToAdd());
        List<Long> imageIdsToRemove = getSafeList(info.getImageIdsToRemove());

        List<StudyImage> images = studyImageRepository.findAllByStudyId(study.getId());

        validateAllIdsPresent(
                imageIdsToRemove,
                images,
                StudyImage::getId,
                "some images not found"
        );
        validateSizeLimit(
                images.size() + imageUrlsToAdd.size() - imageIdsToRemove.size(),
                IMAGE_LIMIT,
                "image limit deviation"
        );

        if (isListNotEmpty(imageIdsToRemove))
            studyImageRepository.deleteAllByIds(imageIdsToRemove);

        if (isListNotEmpty(imageUrlsToAdd))
            studyImageRepository.batchInsert(imageUrlsToAdd, study);
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
            throw new GlobalException(NOT_VALID, "Already Bookmarked");
        }
    }

    private void throwIfAlreadyLiked(Long studyId, UUID userId) {
        if (studyLikeRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new GlobalException(NOT_VALID, "Already Liked");
        }
    }
}
