package com.jj.swm.domain.studyroom.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.fixture.StudyRoomFixture;
import com.jj.swm.domain.studyroom.fixture.StudyRoomQnaFixture;
import com.jj.swm.domain.studyroom.dto.request.UpsertStudyRoomQnaRequest;
import com.jj.swm.domain.studyroom.dto.response.CreateStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.dto.response.UpdateStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import com.jj.swm.domain.studyroom.repository.StudyRoomQnaRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomRepository;
import com.jj.swm.domain.user.UserFixture;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup/studyroom.sql")
class StudyRoomQnaCommandServiceTest extends IntegrationContainerSupporter {

    @Autowired private UserRepository userRepository;
    @Autowired private StudyRoomRepository studyRoomRepository;
    @Autowired private StudyRoomQnaRepository qnaRepository;

    @Autowired private StudyRoomQnaCommandService commandService;

    private StudyRoom studyRoom;
    private StudyRoomQna studyRoomQna;
    private User qnaUser;

    @BeforeEach
    void setUp() {
        User user = userRepository.saveAndFlush(UserFixture.createRoomAdmin());
        studyRoom = studyRoomRepository.saveAndFlush(StudyRoomFixture.createStudyRoom(user));

        qnaUser = userRepository.saveAndFlush(UserFixture.createUserWithUUID());
        studyRoomQna = qnaRepository.saveAndFlush(StudyRoomQnaFixture.createStudyRoomQna(studyRoom, qnaUser));
    }

    @Test
    @DisplayName("스터디 룸 Qna Parent가 없는 댓글 생성에 성공한다.")
    void createQnaNotExistsParent_Success(){
        //given
        UpsertStudyRoomQnaRequest request = UpsertStudyRoomQnaRequest.builder()
                .comment("test")
                .build();

        User user = UserFixture.createUserWithUUID();
        user = userRepository.saveAndFlush(user);

        //when
        CreateStudyRoomQnaResponse response
                = commandService.createQna(request, studyRoom.getId(), null, user.getId());

        //then
        StudyRoomQna studyRoomQna = qnaRepository.findById(response.getStudyRoomQnaId()).get();

        assertEquals(request.getComment(), studyRoomQna.getComment());
    }

    @Test
    @DisplayName("스터디 룸 Qna Parent가 있는 경우 본인 댓글의 대댓글 생성에 성공한다.")
    void createQnaExistsParent_Success(){
        //given
        UpsertStudyRoomQnaRequest request = UpsertStudyRoomQnaRequest.builder()
                .comment("test2")
                .build();

        //when
        CreateStudyRoomQnaResponse response =
                commandService.createQna(request, studyRoom.getId(), studyRoomQna.getId(), qnaUser.getId());

        //then
        StudyRoomQna studyRoomQna2 = qnaRepository.findById(response.getStudyRoomQnaId()).get();
        assertEquals(request.getComment(), studyRoomQna2.getComment());
    }

    @Test
    @DisplayName("스터디 룸 Qna Parent가 있는 경우 룸 관리자가 대댓글 생성에 성공한다.")
    void createQnaExistsParentWithRoomAdmin_Success(){
        //given
        UpsertStudyRoomQnaRequest request = UpsertStudyRoomQnaRequest.builder()
                .comment("test2")
                .build();

        UUID roomAdminId = studyRoom.getUser().getId();

        //when
        commandService.createQna(request, studyRoom.getId(), studyRoomQna.getId(), roomAdminId);

        //then
        StudyRoomQna studyRoomQna2 = qnaRepository.findById(2L).get();

        assertEquals(request.getComment(), studyRoomQna2.getComment());
        assertEquals(studyRoomQna.getId(), studyRoomQna2.getParent().getId());
    }

    @Test
    @DisplayName("스터디 룸 Qna Parent가 있는 경우 룸 관리자나 본인 댓글의 아닌 경우 대댓글 생성에 실패한다.")
    void createQnaExistsParentWithRoomAdmin_Fail(){
        //given
        UpsertStudyRoomQnaRequest request = UpsertStudyRoomQnaRequest.builder()
                .comment("test2")
                .build();

        UUID notValidUserId = UUID.randomUUID();

        //when && then
        assertThatThrownBy(() -> commandService.createQna(
                request, studyRoom.getId(), studyRoomQna.getId(), notValidUserId)).isInstanceOf(GlobalException.class);
    }

    @Test
    @DisplayName("스터디 룸 Qna 수정에 성공한다.")
    void updateQna_Success(){
        //given
        UpsertStudyRoomQnaRequest request = UpsertStudyRoomQnaRequest.builder()
                .comment("update_test")
                .build();

        //when
        UpdateStudyRoomQnaResponse response
                = commandService.updateQna(request, studyRoomQna.getId(), qnaUser.getId());

        //then
        studyRoomQna = qnaRepository.findById(studyRoomQna.getId()).get();

        assertEquals(request.getComment(), studyRoomQna.getComment());
        assertThat(response.getUpdateAt()).isNotNull();
    }

    @Test
    @DisplayName("스터디 룸 Qna 작성한 사람이 아닌 경우 수정에 실패한다.")
    void updateQna_FailWhenNotAuthor(){
        //given
        UpsertStudyRoomQnaRequest request = UpsertStudyRoomQnaRequest.builder()
                .comment("update_test")
                .build();

        UUID notValidUserId = UUID.randomUUID();

        //when & then
        assertThatThrownBy(() -> commandService.updateQna(
                request, studyRoomQna.getId(), notValidUserId)).isInstanceOf(GlobalException.class);
    }

    @Test
    @DisplayName("스터디 룸 Qna 삭제에 성공한다.")
    void deleteQna_Success(){
        //given
        StudyRoomQna childQna1 = StudyRoomQna.of(
                "test",
                studyRoom,
                qnaUser
        );

        StudyRoomQna childQna2 = StudyRoomQna.of(
                "test",
                studyRoom,
                qnaUser
        );

        childQna1.addParent(studyRoomQna);
        childQna2.addParent(studyRoomQna);
        qnaRepository.saveAllAndFlush(Arrays.asList(childQna1, childQna2));

        //when
        commandService.deleteQna(studyRoomQna.getId(), qnaUser.getId());

        //then
        long count = qnaRepository.count();

        assertThat(count).isZero();
    }
}
