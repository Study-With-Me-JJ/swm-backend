package com.jj.swm.domain.studyroom.service;

import com.jj.swm.domain.studyroom.dto.request.*;
import com.jj.swm.domain.studyroom.dto.request.update.dayoff.StudyRoomDayOffModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.dayoff.StudyRoomDayOffUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.update.image.StudyRoomImageModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.image.StudyRoomImageUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.update.option.StudyRoomOptionInfoModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.option.StudyRoomOptionUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.update.reservationType.StudyRoomReservationTypeModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.reservationType.StudyRoomReservationTypeUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.update.tag.StudyRoomTagModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.tag.StudyRoomTagUpdateRequest;
import com.jj.swm.domain.studyroom.dto.request.update.type.StudyRoomTypeInfoModifyRequest;
import com.jj.swm.domain.studyroom.dto.request.update.type.StudyRoomTypeUpdateRequest;
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

    @Transactional
    public void create(StudyRoomCreateRequest request, UUID userId){
        User user = validateRoomAdmin(userId);

        StudyRoom studyRoom = StudyRoom.of(request);
        studyRoom.modifyRoomAdmin(user);

        studyRoom = studyRoomRepository.save(studyRoom);

        createAllOfStudyRoomRelatedInfo(request, studyRoom);
    }

    private void createAllOfStudyRoomRelatedInfo(StudyRoomCreateRequest request, StudyRoom studyRoom) {
        validateDayOffs(request.getDayOffs(), studyRoom);
        validateTags(request.getTags(), studyRoom);
        imageRepository.batchInsert(request.getImageUrls(), studyRoom);
        optionInfoRepository.batchInsert(request.getOptions(), studyRoom);
        typeInfoRepository.batchInsert(request.getTypes(), studyRoom);
        reserveTypeRepository.batchInsert(request.getReservationTypes(), studyRoom);
    }

    @Transactional
    public void update(StudyRoomUpdateRequest request, UUID userId){
        StudyRoom studyRoom = validateStudyRoomWithUserId(request.getStudyRoomId(), userId);

        studyRoom.modifyStudyRoom(request);

        imageModifyLogic(request.getImageModification(), studyRoom);
    }

    @Transactional
    public void updateTag(StudyRoomTagModifyRequest request, UUID userId) {
        StudyRoom studyRoom = validateStudyRoomWithUserId(request.getStudyRoomId(), userId);

        tagModifyLogic(request, studyRoom);
    }

    @Transactional
    public void updateDayOff(StudyRoomDayOffModifyRequest request, UUID userId) {
        StudyRoom studyRoom = validateStudyRoomWithUserId(request.getStudyRoomId(), userId);

        dayOffModifyLogic(request, studyRoom);
    }

    @Transactional
    public void updateOption(StudyRoomOptionInfoModifyRequest request, UUID userId) {
        StudyRoom studyRoom = validateStudyRoomWithUserId(request.getStudyRoomId(), userId);

        optionModifyLogic(request, studyRoom);
    }

    @Transactional
    public void updateType(StudyRoomTypeInfoModifyRequest request, UUID userId) {
        StudyRoom studyRoom = validateStudyRoomWithUserId(request.getStudyRoomId(), userId);

        typeModifyLogic(request, studyRoom);
    }

    @Transactional
    public void updateReserveType(StudyRoomReservationTypeModifyRequest request, UUID userId) {
        StudyRoom studyRoom = validateStudyRoomWithUserId(request.getStudyRoomId(), userId);

        reserveTypeModifyLogic(request, studyRoom);
    }

    @Transactional
    public void delete(StudyRoomDeleteRequest request, UUID userId) {
        StudyRoom studyRoom = validateStudyRoomWithUserId(request.getStudyRoomId(), userId);

        imageRepository.deleteAllByIdInBatch(List.of(studyRoom.getId()));
        tagRepository.deleteAllByIdInBatch(List.of(studyRoom.getId()));
        optionInfoRepository.deleteAllByIdInBatch(List.of(studyRoom.getId()));
        typeInfoRepository.deleteAllByIdInBatch(List.of(studyRoom.getId()));
        reserveTypeRepository.deleteAllByIdInBatch(List.of(studyRoom.getId()));
        dayOffRepository.deleteAllByIdInBatch(List.of(studyRoom.getId()));
        studyRoomRepository.delete(studyRoom);

        // 좋아요, 이용후기, 이용후기 답글, QnA, QnA 답글, 좋아요 수, 북마크 수 삭제 필요
    }

    private void imageModifyLogic(StudyRoomImageModifyRequest imageModification, StudyRoom studyRoom) {
        if (imageModification != null) {
            if (imageModification.getImagesToAdd() != null && !imageModification.getImagesToAdd().isEmpty())
                imageRepository.batchInsert(imageModification.getImagesToAdd(), studyRoom);
            if (imageModification.getImagesToUpdate() != null && !imageModification.getImagesToUpdate().isEmpty())
                updateImages(imageModification.getImagesToUpdate(), studyRoom);
            if (imageModification.getImageIdsToRemove() != null && !imageModification.getImageIdsToRemove().isEmpty())
                removeImages(imageModification.getImageIdsToRemove(), studyRoom);
        }
    }

    private void updateImages(List<StudyRoomImageUpdateRequest> imagesToUpdate, StudyRoom studyRoom) {
        Map<Long, String> imageMap = new HashMap<>();
        List<Long> imageIds = new ArrayList<>();

        for (StudyRoomImageUpdateRequest imageToUpdate : imagesToUpdate) {
            imageMap.put(imageToUpdate.getImageId(), imageToUpdate.getImageUrl());
            imageIds.add(imageToUpdate.getImageId());
        }

        List<StudyRoomImage> studyRoomImages = imageRepository.findAllByIdInAndStudyRoom(imageIds, studyRoom);

        if (studyRoomImages.size() != imagesToUpdate.size()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some image not matching StudyRoom.");
        }

        studyRoomImages.forEach(studyRoomImage ->
                studyRoomImage.modifyImageUrl(imageMap.get(studyRoomImage.getId()))
        );
    }

    private void removeImages(List<Long> imageIdsToRemove, StudyRoom studyRoom) {
        int size = imageRepository.countStudyRoomImageByIdInAndStudyRoom(imageIdsToRemove, studyRoom);

        if(size == imageIdsToRemove.size()){
            imageRepository.deleteAllByIdInBatch(imageIdsToRemove);
        } else{
            throw new GlobalException(ErrorCode.NOT_VALID, "Some image not matching StudyRoom.");
        }
    }

    private void tagModifyLogic(StudyRoomTagModifyRequest request, StudyRoom studyRoom) {
        if (request.getTagsToAdd() != null && !request.getTagsToAdd().isEmpty())
            tagRepository.batchInsert(request.getTagsToAdd(), studyRoom);
        if (request.getTagsToUpdate() != null && !request.getTagsToUpdate().isEmpty())
            updateTags(request.getTagsToUpdate(), studyRoom);
        if (request.getTagIdsToRemove() != null && !request.getTagIdsToRemove().isEmpty())
            removeTags(request.getTagIdsToRemove(), studyRoom);
    }

    private void updateTags(List<StudyRoomTagUpdateRequest> tagsToUpdate, StudyRoom studyRoom) {
        Map<Long, String> tagMap = new HashMap<>();
        List<Long> tagIds = new ArrayList<>();

        for (StudyRoomTagUpdateRequest tagToUpdate : tagsToUpdate) {
            tagMap.put(tagToUpdate.getTagId(), tagToUpdate.getTag());
            tagIds.add(tagToUpdate.getTagId());
        }

        List<StudyRoomTag> studyRoomTags = tagRepository.findAllByIdInAndStudyRoom(tagIds, studyRoom);

        if (studyRoomTags.size() != tagsToUpdate.size()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some image not matching StudyRoom.");
        }

        studyRoomTags.forEach(studyRoomTag ->
                studyRoomTag.modifyTag(tagMap.get(studyRoomTag.getId()))
        );
    }

    private void removeTags(List<Long> tagIdsToRemove, StudyRoom studyRoom) {
        int size = tagRepository.countStudyRoomTagByIdInAndStudyRoom(tagIdsToRemove, studyRoom);

        if(size == tagIdsToRemove.size()){
            tagRepository.deleteAllByIdInBatch(tagIdsToRemove);
        } else{
            throw new GlobalException(ErrorCode.NOT_VALID, "Some tag not matching StudyRoom.");
        }
    }

    private void dayOffModifyLogic(StudyRoomDayOffModifyRequest request, StudyRoom studyRoom) {
        if (request.getDayOffsToAdd() != null && !request.getDayOffsToAdd().isEmpty())
            dayOffRepository.batchInsert(request.getDayOffsToAdd(), studyRoom);
        if (request.getDayOffsToUpdate() != null && !request.getDayOffsToUpdate().isEmpty())
            updateDayOffs(request.getDayOffsToUpdate(), studyRoom);
        if (request.getDayOffIdsToRemove() != null && !request.getDayOffIdsToRemove().isEmpty())
            removeDayOffs(request.getDayOffIdsToRemove(), studyRoom);
    }

    private void updateDayOffs(List<StudyRoomDayOffUpdateRequest> dayOffsToUpdate, StudyRoom studyRoom) {
        Map<Long, DayOfWeek> dayOffMap = new HashMap<>();
        List<Long> dayOffIds = new ArrayList<>();

        for (StudyRoomDayOffUpdateRequest dayOffToUpdate : dayOffsToUpdate) {
            dayOffMap.put(dayOffToUpdate.getDayOffId(), dayOffToUpdate.getDayOff());
            dayOffIds.add(dayOffToUpdate.getDayOffId());
        }

        List<StudyRoomDayOff> studyRoomDayOffs = dayOffRepository.findAllByIdInAndStudyRoom(dayOffIds, studyRoom);

        if (studyRoomDayOffs.size() != dayOffsToUpdate.size()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some day-off not matching StudyRoom.");
        }

        studyRoomDayOffs.forEach(studyRoomDayOff ->
                studyRoomDayOff.modifyDayOff(dayOffMap.get(studyRoomDayOff.getId()))
        );
    }

    private void removeDayOffs(List<Long> dayOffIdsToRemove, StudyRoom studyRoom) {
        int size = dayOffRepository.countStudyRoomDayOffByIdInAndStudyRoom(dayOffIdsToRemove, studyRoom);

        if (size == dayOffIdsToRemove.size()) {
            dayOffRepository.deleteAllByIdInBatch(dayOffIdsToRemove);
        } else {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some day-off not matching StudyRoom.");
        }
    }

    private void optionModifyLogic(StudyRoomOptionInfoModifyRequest request, StudyRoom studyRoom) {
        if (request.getOptionsToAdd() != null && !request.getOptionsToAdd().isEmpty())
            optionInfoRepository.batchInsert(request.getOptionsToAdd(), studyRoom);
        if (request.getOptionsToUpdate() != null && !request.getOptionsToUpdate().isEmpty())
            updateOptions(request.getOptionsToUpdate(), studyRoom);
        if (request.getOptionsIdsToRemove() != null && !request.getOptionsIdsToRemove().isEmpty())
            removeOptions(request.getOptionsIdsToRemove(), studyRoom);
    }

    private void updateOptions(List<StudyRoomOptionUpdateRequest> optionsToUpdate, StudyRoom studyRoom) {
        Map<Long, StudyRoomOption> optionMap = new HashMap<>();
        List<Long> optionIds = new ArrayList<>();

        for (StudyRoomOptionUpdateRequest optionToUpdate : optionsToUpdate) {
            optionMap.put(optionToUpdate.getOptionId(), optionToUpdate.getOption());
            optionIds.add(optionToUpdate.getOptionId());
        }

        List<StudyRoomOptionInfo> studyRoomOptions = optionInfoRepository.findAllByIdInAndStudyRoom(optionIds, studyRoom);

        if (studyRoomOptions.size() != optionsToUpdate.size()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some option not matching StudyRoom.");
        }

        studyRoomOptions.forEach(studyRoomOption ->
                studyRoomOption.modifyOption(optionMap.get(studyRoomOption.getId()))
        );
    }

    private void removeOptions(List<Long> optionIdsToRemove, StudyRoom studyRoom) {
        int size = optionInfoRepository.countStudyRoomOptionInfoByIdInAndStudyRoom(optionIdsToRemove, studyRoom);

        if (size == optionIdsToRemove.size()) {
            optionInfoRepository.deleteAllByIdInBatch(optionIdsToRemove);
        } else {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some option not matching StudyRoom.");
        }
    }

    private void typeModifyLogic(StudyRoomTypeInfoModifyRequest request, StudyRoom studyRoom) {
        if (request.getTypesToAdd() != null && !request.getTypesToAdd().isEmpty())
            typeInfoRepository.batchInsert(request.getTypesToAdd(), studyRoom);
        if (request.getTypesToUpdate() != null && !request.getTypesToUpdate().isEmpty())
            updateTypes(request.getTypesToUpdate(), studyRoom);
        if (request.getTypeIdsToRemove() != null && !request.getTypeIdsToRemove().isEmpty())
            removeTypes(request.getTypeIdsToRemove(), studyRoom);
    }

    private void updateTypes(List<StudyRoomTypeUpdateRequest> typesToUpdate, StudyRoom studyRoom) {
        Map<Long, StudyRoomType> typeMap = new HashMap<>();
        List<Long> typeIds = new ArrayList<>();

        for (StudyRoomTypeUpdateRequest typeToUpdate : typesToUpdate) {
            typeMap.put(typeToUpdate.getTypeId(), typeToUpdate.getType());
            typeIds.add(typeToUpdate.getTypeId());
        }

        List<StudyRoomTypeInfo> studyRoomTypes = typeInfoRepository.findAllByIdInAndStudyRoom(typeIds, studyRoom);

        if (studyRoomTypes.size() != typesToUpdate.size()) {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some type not matching StudyRoom.");
        }

        studyRoomTypes.forEach(studyRoomType ->
                studyRoomType.modifyType(typeMap.get(studyRoomType.getId()))
        );
    }

    private void removeTypes(List<Long> typeIdsToRemove, StudyRoom studyRoom) {
        int size = typeInfoRepository.countStudyRoomTypeInfoByIdInAndStudyRoom(typeIdsToRemove, studyRoom);

        if (size == typeIdsToRemove.size()) {
            typeInfoRepository.deleteAllByIdInBatch(typeIdsToRemove);
        } else {
            throw new GlobalException(ErrorCode.NOT_VALID, "Some type not matching StudyRoom.");
        }
    }

    private void reserveTypeModifyLogic(StudyRoomReservationTypeModifyRequest request, StudyRoom studyRoom) {
        if (request.getReservationTypesToAdd() != null)
            reserveTypeRepository.batchInsert(request.getReservationTypesToAdd(), studyRoom);
        if (request.getReservationTypesToUpdate() != null && !request.getReservationTypesToUpdate().isEmpty())
            updateReserveTypes(request.getReservationTypesToUpdate(), studyRoom);
        if (request.getReservationTypeIdsToRemove() != null && !request.getReservationTypeIdsToRemove().isEmpty())
            removeReserveTypes(request.getReservationTypeIdsToRemove(), studyRoom);
    }

    private void updateReserveTypes(
            List<StudyRoomReservationTypeUpdateRequest> reservationTypesToUpdate, StudyRoom studyRoom
    ) {
        Map<Long, StudyRoomReservationTypeCreateRequest> reserveTypeMap = new HashMap<>();
        List<Long> reserveTypeIds = new ArrayList<>();

        for (StudyRoomReservationTypeUpdateRequest reserveTypeToUpdate : reservationTypesToUpdate) {
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
        return studyRoomRepository.findByIdAndUserId(studyRoomId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoom Not Found"));
    }

}
