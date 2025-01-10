package com.jj.swm.config.studyroom;

import com.jj.swm.domain.studyroom.service.StudyRoomReviewCommandService;
import com.jj.swm.domain.studyroom.service.StudyRoomReviewQueryService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class StudyRoomReviewTestConfig {

    @Autowired
    private StudyRoomReviewCommandService reviewCommandService;

    @Autowired
    private StudyRoomReviewQueryService reviewQueryService;

    @Bean
    @Primary
    public StudyRoomReviewCommandService testReviewCommandService() {
        return reviewCommandService;
    }

    @Bean
    public StudyRoomReviewCommandService mockReviewCommandService() {
        return Mockito.mock(StudyRoomReviewCommandService.class);
    }

    @Bean
    public StudyRoomReviewCommandService spyReviewCommandService() {
        return Mockito.spy(reviewCommandService);
    }

    @Bean
    @Primary
    public StudyRoomReviewQueryService testReviewQueryService() {
        return reviewQueryService;
    }

    @Bean
    public StudyRoomReviewQueryService mockReviewQueryService() {
        return Mockito.mock(StudyRoomReviewQueryService.class);
    }

    @Bean
    public StudyRoomReviewQueryService spyReviewQueryService() {
        return Mockito.spy(reviewQueryService);
    }
}
