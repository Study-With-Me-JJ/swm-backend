package com.jj.swm.domain.studyroom.qna.service;

import com.jj.swm.domain.studyroom.qna.dto.request.UpsertStudyRoomQnaRequest;
import com.jj.swm.domain.studyroom.qna.dto.response.CreateStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.qna.dto.response.UpdateStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.core.entity.StudyRoom;
import com.jj.swm.domain.studyroom.qna.entity.StudyRoomQna;
import com.jj.swm.domain.studyroom.qna.repository.StudyRoomQnaRepository;
import com.jj.swm.domain.studyroom.core.repository.StudyRoomRepository;
import com.jj.swm.domain.user.core.entity.User;
import com.jj.swm.domain.user.core.repository.UserRepository;
import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyRoomQnaCommandService {

    private final UserRepository userRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomQnaRepository qnaRepository;

    @Transactional
    public CreateStudyRoomQnaResponse createQna(
            UpsertStudyRoomQnaRequest request,
            Long studyRoomId,
            Long parentId,
            UUID userId
    ) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithUser(studyRoomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "StudyRoom Not Found"));

        User user = userRepository.getReferenceById(userId);

        StudyRoomQna studyRoomQna = StudyRoomQna.of(
                request.getComment(),
                studyRoom,
                user
        );

        if(parentId != null){
            StudyRoomQna parent = qnaRepository.findByIdWithParent(parentId, userId)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Qna Not Found"));

            parent = parent.getParent() == null ? parent : parent.getParent();

            if(isNotQnaAuthorAndRoomAdmin(parent, user, studyRoom))
                throw new GlobalException(ErrorCode.FORBIDDEN, "Qna Create Access Denied");

            studyRoomQna.addParent(parent);
        }

        qnaRepository.save(studyRoomQna);
        return CreateStudyRoomQnaResponse.from(studyRoomQna);
    }

    @Transactional
    public UpdateStudyRoomQnaResponse updateQna(
            UpsertStudyRoomQnaRequest request,
            Long studyRoomQnaId,
            UUID userId
    ) {
        StudyRoomQna studyRoomQna = qnaRepository.findByIdAndUserId(studyRoomQnaId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Qna Not Found"));

        studyRoomQna.modify(request.getComment());

        return UpdateStudyRoomQnaResponse.from(studyRoomQna);
    }

    @Transactional
    public void deleteQna(Long studyRoomQnaId, UUID userId) {
        qnaRepository.deleteAllByIdOrParentIdAndUserId(studyRoomQnaId, userId);
    }

    private boolean isNotQnaAuthorAndRoomAdmin(StudyRoomQna parent, User user, StudyRoom studyRoom){
        return !parent.getUser().getId().equals(user.getId())
                && !studyRoom.getUser().getId().equals(user.getId());
    }
}
