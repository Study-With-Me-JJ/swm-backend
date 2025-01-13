package com.jj.swm.domain.studyroom.service;

import com.jj.swm.IntegrationContainerSupporter;
import com.jj.swm.domain.studyroom.dto.response.GetStudyRoomQnaResponse;
import com.jj.swm.domain.studyroom.entity.StudyRoom;
import com.jj.swm.domain.studyroom.entity.StudyRoomQna;
import com.jj.swm.domain.studyroom.fixture.StudyRoomFixture;
import com.jj.swm.domain.studyroom.repository.StudyRoomQnaRepository;
import com.jj.swm.domain.studyroom.repository.StudyRoomRepository;
import com.jj.swm.domain.user.entity.User;
import com.jj.swm.domain.user.fixture.UserFixture;
import com.jj.swm.domain.user.repository.UserRepository;
import com.jj.swm.global.common.dto.PageResponse;
import com.jj.swm.global.common.enums.PageSize;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class StudyRoomQnaQueryServiceIntegrationTest extends IntegrationContainerSupporter {

    // Target Service Bean
    @Autowired private StudyRoomQnaQueryService queryService;

    // Repository Bean
    @Autowired private StudyRoomQnaRepository qnaRepository;

    // Repository Bean For Test
    @Autowired private StudyRoomRepository studyRoomRepository;
    @Autowired private UserRepository userRepository;

    //EntityManager
    @Autowired private EntityManager entityManager;

    private User roomAdmin;
    private StudyRoom studyRoom;

    @BeforeEach
    void setUp() {
        roomAdmin = UserFixture.createRoomAdmin();
        userRepository.save(roomAdmin);
        studyRoom = studyRoomRepository.save(StudyRoomFixture.createStudyRoom(roomAdmin));
    }

    @Test
    @DisplayName("스터디 룸 QnA 페이지네이션 조회에 성공한다.")
    void studyRoomQna_getStudyRoomQnas_Success() {
        //given
        for(int i = 0; i < PageSize.StudyRoomQna; i++){
            User user = UserFixture.createUserWithUUID();
            user = userRepository.save(user);

            StudyRoomQna studyRoomQna = StudyRoomQna.builder()
                    .studyRoom(studyRoom)
                    .comment("test" + i)
                    .user(user)
                    .build();

            qnaRepository.save(studyRoomQna);
        }

        //when
        PageResponse<GetStudyRoomQnaResponse> response
                = queryService.getStudyRoomQnas(studyRoom.getId(), 0);

        //then
        long count = qnaRepository.count();
        assertThat(count).isEqualTo(PageSize.StudyRoomQna);
        assertThat(response.getData().size()).isEqualTo(PageSize.StudyRoomQna);
        assertThat(response.getData().getLast().getComment()).isEqualTo("test0");
    }

    @Test
    @DisplayName("스터디 룸 QnA의 자식 조회에 성공한다.")
    void studyRoomQna_getStudyRoomQnas_retrieveChild_Success() {
        //given
        User user = UserFixture.createUserWithUUID();
        userRepository.save(user);

        StudyRoomQna qna = StudyRoomQna.builder()
                .comment("parent")
                .studyRoom(studyRoom)
                .user(user)
                .children(new ArrayList<>())
                .build();

        qnaRepository.save(qna);

        StudyRoomQna qnaChild = StudyRoomQna.builder()
                .comment("child")
                .studyRoom(studyRoom)
                .user(roomAdmin)
                .build();
        qnaChild.addParent(qna);

        qnaRepository.save(qnaChild);

        entityManager.flush();
        entityManager.clear();

        //when
        PageResponse<GetStudyRoomQnaResponse> response
                = queryService.getStudyRoomQnas(studyRoom.getId(), 0);

        //then
        assertThat(response.getData().size()).isEqualTo(1);
        assertThat(response.getData().getFirst().getComment()).isEqualTo("parent");
        assertThat(response.getData().getFirst().getChildQnas().getFirst().getComment()).isEqualTo("child");
    }
}
