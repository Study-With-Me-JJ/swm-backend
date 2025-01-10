package com.jj.swm.config.studyroom;

import com.jj.swm.domain.studyroom.service.StudyRoomCommandService;
import com.jj.swm.domain.studyroom.service.StudyRoomQueryService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class StudyRoomTestConfig {

    @Autowired
    private StudyRoomCommandService studyRoomCommandService;

    @Autowired
    private StudyRoomQueryService studyRoomQueryService;

    @Bean
    @Primary
    public StudyRoomCommandService testStudyRoomCommandService() {
        return studyRoomCommandService;
    }

    @Bean
    public StudyRoomCommandService mockStudyRoomCommandService() {
        return Mockito.mock(StudyRoomCommandService.class);
    }

    @Bean
    public StudyRoomCommandService spyStudyRoomCommandService() {
        return Mockito.spy(studyRoomCommandService);
    }

    @Bean
    @Primary
    public StudyRoomQueryService testStudyRoomQueryService() {
        return studyRoomQueryService;
    }

    @Bean
    public StudyRoomQueryService mockStudyRoomQueryService() {
        return Mockito.mock(StudyRoomQueryService.class);
    }

    @Bean
    public StudyRoomQueryService spyStudyRoomQueryService() {
        return Mockito.spy(studyRoomQueryService);
    }
}
