package com.jj.swm;

import com.jj.swm.config.TestConfig;
import com.jj.swm.config.studyroom.StudyRoomQnaTestConfig;
import com.jj.swm.config.studyroom.StudyRoomReviewTestConfig;
import com.jj.swm.config.studyroom.StudyRoomTestConfig;
import com.jj.swm.crawling.holaworld.service.HolaWorldCrawlingService;
import com.jj.swm.global.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Testcontainers
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrationContainerSupporter {

    private static final String REDIS_IMAGE = "bitnami/valkey:8.0.1";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = "password";

    private static final String POSTGRES_IMAGE = "postgres:17";
    private static final GenericContainer REDIS_CONTAINER;

    @Container
    @ServiceConnection
    static PostgreSQLContainer POSTGRES_CONTAINER = new PostgreSQLContainer(DockerImageName.parse(POSTGRES_IMAGE));

    static {
        REDIS_CONTAINER = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
                .withExposedPorts(REDIS_PORT)
                .withReuse(true)
                .withEnv("VALKEY_PASSWORD", "password");

        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        // Redis
        registry.add("redis.host", REDIS_CONTAINER::getHost);
        registry.add("redis.port", () -> String.valueOf(REDIS_CONTAINER.getMappedPort(REDIS_PORT)));
        registry.add("redis.password", () -> REDIS_PASSWORD);
    }
}
