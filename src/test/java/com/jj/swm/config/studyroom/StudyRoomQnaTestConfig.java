package com.jj.swm.config.studyroom;

import com.jj.swm.domain.studyroom.qna.service.StudyRoomQnaCommandService;
import com.jj.swm.domain.studyroom.qna.service.StudyRoomQnaQueryService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class StudyRoomQnaTestConfig {

    @Autowired
    private StudyRoomQnaCommandService qnaCommandService;

    @Autowired
    private StudyRoomQnaQueryService qnaQueryService;

    @Bean
    @Primary
    public StudyRoomQnaCommandService testQnaCommandService() {
        return qnaCommandService;
    }

    @Bean
    public StudyRoomQnaCommandService mockQnaCommandService() {
        return Mockito.mock(StudyRoomQnaCommandService.class);
    }

    @Bean
    public StudyRoomQnaCommandService spyQnaCommandService() {
        return Mockito.spy(qnaCommandService);
    }

    @Bean
    @Primary
    public StudyRoomQnaQueryService testQnaQueryService() {
        return qnaQueryService;
    }

    @Bean
    public StudyRoomQnaQueryService mockQnaQueryService() {
        return Mockito.mock(StudyRoomQnaQueryService.class);
    }

    @Bean
    public StudyRoomQnaQueryService spyQnaQueryService() {
        return Mockito.spy(qnaQueryService);
    }
}
