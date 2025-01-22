package com.jj.swm.config;

import com.jj.swm.config.auth.AuthTestConfig;
import com.jj.swm.config.studyroom.StudyRoomQnaTestConfig;
import com.jj.swm.config.studyroom.StudyRoomReviewTestConfig;
import com.jj.swm.config.studyroom.StudyRoomTestConfig;
import com.jj.swm.config.user.UserTestConfig;
import com.jj.swm.global.config.JpaConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({
        JpaConfig.class,
        StudyRoomTestConfig.class,
        StudyRoomQnaTestConfig.class,
        StudyRoomReviewTestConfig.class,
        UserTestConfig.class,
        AuthTestConfig.class
})
public class TestConfig {
}
