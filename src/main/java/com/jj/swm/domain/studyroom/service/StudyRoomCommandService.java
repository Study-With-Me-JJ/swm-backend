package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.request.*;
import com.jj.swm.domain.studyroom.dto.request.update.ModifyStudyRoomDayOffRequest;
import com.jj.swm.domain.studyroom.dto.request.update.ModifyStudyRoomImageRequest;
import com.jj.swm.domain.studyroom.dto.request.update.ModifyStudyRoomOptionInfoRequest;
import com.jj.swm.domain.studyroom.dto.request.update.ModifyStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.dto.request.update.UpdateStudyRoomReservationTypeRequest;
import com.jj.swm.domain.studyroom.dto.request.update.ModifyStudyRoomTagRequest;
import com.jj.swm.domain.studyroom.dto.request.update.ModifyStudyRoomTypeInfoRequest;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomBookmarkResponse;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomLikeResponse;
import com.jj.swm.domain.studyroom.entity.*;
import com.jj.swm.domain.studyroom.repository.*;
import com.jj.swm.domain.user.entity.RoleType;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyRoomCommandService {

    private final StudyRoomRepository studyRoomRepository;
    private final UserRepository userRepository;
    private final StudyRoomDayOffRepository dayOffRepository;
    private final StudyRoomImageRepository imageRepository;
    private final StudyRoomOptionInfoRepository optionInfoRepository;
    private final StudyRoomTypeInfoRepository typeInfoRepository;
    private final StudyRoomReserveTypeRepository reserveTypeRepository;
    private final StudyRoomTagRepository tagRepository;
    private final StudyRoomLikeRepository likeRepository;
    private final StudyRoomBookmarkRepository bookmarkRepository;
    private final StudyRoomReviewRepository reviewRepository;
    private final StudyRoomReviewReplyRepository reviewReplyRepository;
    private final StudyRoomReviewImageRepository reviewImageRepository;
    private final StudyRoomQnaRepository qnaRepository;

    @Transactional
    public void create(CreateStudyRoomRequest request, UUID userId){
        User user = validateRoomAdmin(userId);

        StudyRoom studyRoom = StudyRoom.of(request);
        studyRoom.modifyRoomAdmin(user);

        studyRoom = studyRoomRepository.save(studyRoom);

        createAllOfStudyRoomRelatedInfo(request, studyRoom);
    }

    private void createAllOfStudyRoomRelatedInfo(CreateStudyRoomRequest request, StudyRoom studyRoom) {
        validateDayOffs(request.getDayOffs(), studyRoom);
        validateTags(request.getTags(), studyRoom);
        imageRepository.batchInsert(request.getImageUrls(), studyRoom);
        optionInfoRepository.batchInsert(request.getOptions(), studyRoom);
        typeInfoRepository.batchInsert(request.getTypes(), studyRoom);
        reserveTypeRepository.batchInsert(request.getReservationTypes(), studyRoom);
    }

    @Transactional
    public void update(
            UpdateStudyRoomRequest request,
            Long studyRoomId,
            UUID userId
    ){
        StudyRoom studyRoom = validateStudyRoomWithUserId(studyRoomId, userId);

        studyRoom.modifyStudyRoom(request);

        imageModifyLogic(request.getImageModification(), studyRoom);
        tagModifyLogic(request.getTagModification(), studyRoom);
        dayOffModifyLogic(request.getDayOffModification(), studyRoom);
    }

    @Transactional
    public void updateSettings(
            UpdateStudyRoomSettingRequest request,
            Long studyRoomId,
            UUID userId
    ) {
        StudyRoom studyRoom = validateStudyRoomWithUserId(studyRoomId, userId);

        optionModifyLogic(request.getOptionInfoModification(), studyRoom);
        typeModifyLogic(request.getTypeInfoModification(), studyRoom);
        reserveTypeModifyLogic(request.getReservationTypeModification(), studyRoom);
    }

    @Transactional
    public void delete(Long studyRoomId, UUID userId) {
        if(!studyRoomRepository.existsByIdAndUserId(studyRoomId, userId))
            throw new GlobalException(ErrorCode.NOT_FOUND, "StudyRoom Not Found");
        else
            deleteStudyRoomLogic(studyRoomId);
    }

    @Transactional
    public CreateStudyRoomLikeResponse createLike(Long studyRoomId, UUID userId) {
        validateExistsLike(studyRoomId, userId);

        StudyRoom studyRoom = validateStudyRoomWithLock(studyRoomId);
        User user = userRepository.getReferenceById(userId);

        StudyRoomLike studyRoomLike = StudyRoomLike.of(studyRoom, user);

        likeRepository.save(studyRoomLike);

        studyRoom.likeStudyRoom();

        return CreateStudyRoomLikeResponse.from(studyRoomLike);
    }

    @Transactional
    public void unLike(Long studyRoomId, UUID userId) {
        StudyRoomLike studyRoomLike = validateLike(studyRoomId, userId);

        StudyRoom studyRoom = validateStudyRoomWithLock(studyRoomId);

        studyRoom.unLikeStudyRoom();
        likeRepository.delete(studyRoomLike);
    }

    @Transactional
    public CreateStudyRoomBookmarkResponse createBookmark(Long studyRoomId, UUID userId) {
        validateExistsBookmark(studyRoomId, userId);
        StudyRoom studyRoom = validateStudyRoom(studyRoomId);
        User user = userRepository.getReferenceById(userId);

        StudyRoomBookmark studyRoomBookmark = StudyRoomBookmark.of(studyRoom, user);

        bookmarkRepository.save(studyRoomBookmark);

        return CreateStudyRoomBookmarkResponse.from(studyRoomBookmark);
    }

    @Transactional
    public void unBookmark(Long studyRoomBookmarkId, UUID userId) {
        StudyRoomBookmark studyRoomBookmark = bookmarkRepository.findByIdAndUserId(studyRoomBookmarkId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomBookmark Not Found"));

        bookmarkRepository.delete(studyRoomBookmark);
    }

    private void imageModifyLogic(ModifyStudyRoomImageRequest imageModification, StudyRoom studyRoom) {
        if (imageModification != null) {
            if (imageModification.getImagesToAdd() != null && !imageModification.getImagesToAdd().isEmpty())
                imageRepository.batchInsert(imageModification.getImagesToAdd(), studyRoom);
            if (imageModification.getImageIdsToRemove() != null && !imageModification.getImageIdsToRemove().isEmpty())
                removeImages(imageModification.getImageIdsToRemove(), studyRoom);
        }
    }

    private void removeImages(List<Long> imageIdsToRemove, StudyRoom studyRoom) {
        int size = imageRepository.countStudyRoomImageByIdInAndStudyRoom(imageIdsToRemove, studyRoom);

        if(size == imageIdsToRemove.size()){
            imageRepository.deleteAllByIdInBatch(imageIdsToRemove);
        } else{
            throw new GlobalException(ErrorCode.NOT_VALID, "Some image not matching StudyRoom.");
        }
    }

    private void tagModifyLogic(ModifyStudyRoomTagRequest request, StudyRoom studyRoom) {
        if (request.getTagsToAdd() != null && !request.getTagsToAdd().isEmpty())
            tagRepository.batchInsert(request.getTagsToAdd(), studyRoom);
        if (request.getTagIdsToRemove() != null && !request.getTagIdsToRemove().isEmpty())
            removeTags(request.getTagIdsToRemove(), studyRoom);
    }

    private void removeTags(List<Long> tagIdsToRemove, StudyRoom studyRoom) {
        int size = tagRepository.countStudyRoomTagByIdInAndStudyRoom(tagIdsToRemove, studyRoom);

        if(size == tagIdsToRemove.size()){
            tagRepository.deleteAllByIdInBatch(tagIdsToRemove);
        } else{
            throw new GlobalException(ErrorCode.NOT_VALID, "Some tag not matching StudyRoom.");
        }
    }

    private void dayOffModifyLogic(ModifyStudyRoomDayOffRequest request, StudyRoom studyRoom) {
        if (request.getDayOffsToAdd() != null && !request.getDayOffsToAdd().isEmpty())
            dayOffRepository.batchInsert(request.getDayOffsToAdd(), studyRoom);
        if (request.getDayOffIdsToRemove() != null && !request.getDayOffIdsToRemove().isEmpty())
            removeDayOffs(request.getDayOffIdsToRemove(), studyRoom);
    }

    private void removeDayOffs(List<Long> dayOffIdsToRemove, StudyRoom studyRoom) {
        int size = dayOffRepository.countStudyRoomDayOffByIdInAndStudyRoom(dayOffIdsToRemove, studyRoom);

        if (size == dayOffIdsToRemove.size()) {
            dayOffRepository.deleteAllByIdInBatch(dayOffIdsToRemove);
        } else {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some day-off not matching StudyRoom.");
        }
    }

    private void optionModifyLogic(ModifyStudyRoomOptionInfoRequest request, StudyRoom studyRoom) {
        if (request.getOptionsToAdd() != null && !request.getOptionsToAdd().isEmpty())
            optionInfoRepository.batchInsert(request.getOptionsToAdd(), studyRoom);
        if (request.getOptionsIdsToRemove() != null && !request.getOptionsIdsToRemove().isEmpty())
            removeOptions(request.getOptionsIdsToRemove(), studyRoom);
    }

    private void removeOptions(List<Long> optionIdsToRemove, StudyRoom studyRoom) {
        int size = optionInfoRepository.countStudyRoomOptionInfoByIdInAndStudyRoom(optionIdsToRemove, studyRoom);

        if (size == optionIdsToRemove.size()) {
            optionInfoRepository.deleteAllByIdInBatch(optionIdsToRemove);
        } else {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some option not matching StudyRoom.");
        }
    }

    private void typeModifyLogic(ModifyStudyRoomTypeInfoRequest request, StudyRoom studyRoom) {
        if (request.getTypesToAdd() != null && !request.getTypesToAdd().isEmpty())
            typeInfoRepository.batchInsert(request.getTypesToAdd(), studyRoom);
        if (request.getTypeIdsToRemove() != null && !request.getTypeIdsToRemove().isEmpty())
            removeTypes(request.getTypeIdsToRemove(), studyRoom);
    }

    private void removeTypes(List<Long> typeIdsToRemove, StudyRoom studyRoom) {
        int size = typeInfoRepository.countStudyRoomTypeInfoByIdInAndStudyRoom(typeIdsToRemove, studyRoom);

        if (size == typeIdsToRemove.size()) {
            typeInfoRepository.deleteAllByIdInBatch(typeIdsToRemove);
        } else {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some type not matching StudyRoom.");
        }
    }

    private void reserveTypeModifyLogic(ModifyStudyRoomReservationTypeRequest request, StudyRoom studyRoom) {
        if (request.getReservationTypesToAdd() != null)
            reserveTypeRepository.batchInsert(request.getReservationTypesToAdd(), studyRoom);
        if (request.getReservationTypesToUpdate() != null && !request.getReservationTypesToUpdate().isEmpty())
            updateReserveTypes(request.getReservationTypesToUpdate(), studyRoom);
        if (request.getReservationTypeIdsToRemove() != null && !request.getReservationTypeIdsToRemove().isEmpty())
            removeReserveTypes(request.getReservationTypeIdsToRemove(), studyRoom);
    }

    private void updateReserveTypes(
            List<UpdateStudyRoomReservationTypeRequest> reservationTypesToUpdate, StudyRoom studyRoom
    ) {
        Map<Long, CreateStudyRoomReservationTypeRequest> reserveTypeMap = new HashMap<>();
        List<Long> reserveTypeIds = new ArrayList<>();

        for (UpdateStudyRoomReservationTypeRequest reserveTypeToUpdate : reservationTypesToUpdate) {
            reserveTypeMap.put(reserveTypeToUpdate.getReservationTypeId(), reserveTypeToUpdate.getReservationType());
            reserveTypeIds.add(reserveTypeToUpdate.getReservationTypeId());
        }

        List<StudyRoomReserveType> reserveTypes =
                reserveTypeRepository.findAllByIdInAndStudyRoom(reserveTypeIds, studyRoom);

        if (reserveTypes.size() != reservationTypesToUpdate.size()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some reserve type not matching StudyRoom.");
        }

        reserveTypes.forEach(reserveType ->
                reserveType.modifyReserveType(reserveTypeMap.get(reserveType.getId()))
        );
    }

    private void removeReserveTypes(List<Long> reservationTypeIdsToRemove, StudyRoom studyRoom) {
        int size = reserveTypeRepository.countStudyRoomReserveTypeByIdInAndStudyRoom(
                reservationTypeIdsToRemove, studyRoom);

        if (size == reservationTypeIdsToRemove.size()) {
            reserveTypeRepository.deleteAllByIdInBatch(reservationTypeIdsToRemove);
        } else {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some reserve type not matching StudyRoom.");
        }
    }

    private void deleteStudyRoomLogic(Long studyRoomId) {
        imageRepository.deleteAllByStudyRoomId(studyRoomId);
        tagRepository.deleteAllByStudyRoomId(studyRoomId);
        optionInfoRepository.deleteAllByStudyRoomId(studyRoomId);
        typeInfoRepository.deleteAllByStudyRoomId(studyRoomId);
        reserveTypeRepository.deleteAllByStudyRoomId(studyRoomId);
        dayOffRepository.deleteAllByStudyRoomId(studyRoomId);
        likeRepository.deleteAllByStudyRoomId(studyRoomId);
        bookmarkRepository.deleteAllByStudyRoomId(studyRoomId);

        List<StudyRoomReview> reviews = reviewRepository.findByStudyRoomId(studyRoomId);
        List<Long> reviewIds = reviews.stream()
                        .map(StudyRoomReview::getId)
                        .toList();

        reviewReplyRepository.deleteAllByStudyRoomReviewIdIn(reviewIds);
        reviewImageRepository.deleteAllByStudyRoomReviewIdIn(reviewIds);

        reviewRepository.deleteAllByReviewIds(reviewIds);
        qnaRepository.deleteAllByStudyRoomId(studyRoomId);
        studyRoomRepository.deleteByIdWithJpql(studyRoomId);
    }

    private void validateDayOffs(List<DayOfWeek> dayOffs, StudyRoom studyRoom) {
        // DayOff 생성 및 저장
        if (dayOffs != null && !dayOffs.isEmpty()) {
            dayOffRepository.batchInsert(dayOffs, studyRoom);
        }
    }

    private void validateTags(List<String> tags, StudyRoom studyRoom) {
        // Tag 생성 및 저장
        if (tags != null && !tags.isEmpty()) {
            tagRepository.batchInsert(tags, studyRoom);
        }
    }

    private User validateRoomAdmin(UUID userId) {
        return userRepository.findByIdAndUserRole(userId, RoleType.ROOM_ADMIN)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "User Not Found"));
    }

    private StudyRoom validateStudyRoomWithUserId(Long studyRoomId, UUID userId) {
        return studyRoomRepository.findByIdAndUserIdWithUser(studyRoomId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoom Not Found"));
    }

    private StudyRoom validateStudyRoom(Long studyRoomId) {
        return studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoom Not Found"));
    }

    private StudyRoom validateStudyRoomWithLock(Long studyRoomId) {
        return studyRoomRepository.findByIdWithLock(studyRoomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoom Not Found"));
    }

    private void validateExistsLike(Long studyRoomId, UUID userId) {
        if(likeRepository.existsByStudyRoomIdAndUserId(studyRoomId, userId))
            throw new GlobalException(ErrorCode.NOT_VALID, "Already Liked");
    }

    private StudyRoomLike validateLike(Long studyRoomId, UUID userId) {
        return likeRepository.findByStudyRoomIdAndUserId(studyRoomId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoomLike Not Found"));
    }

    private void validateExistsBookmark(Long studyRoomId, UUID userId) {
        if(bookmarkRepository.existsByStudyRoomIdAndUserId(studyRoomId, userId))
            throw new GlobalException(ErrorCode.NOT_VALID, "Already Bookmarked");
    }

}
