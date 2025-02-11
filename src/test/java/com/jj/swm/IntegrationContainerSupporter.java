package com.jj.swm;

import com.jj.swm.config.TestConfig;
import com.jj.swm.domain.user.core.service.BusinessStatusService;
import com.jj.swm.global.common.service.DiscordNotificationService;
import com.jj.swm.global.common.service.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
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

    static GenericContainer POSTGRES_CONTAINER = new PostgreSQLContainer(DockerImageName.parse(POSTGRES_IMAGE))
            .withDatabaseName("swm")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_USER", "test")
            .withEnv("POSTGRES_PASSWORD", "test")
            .withReuse(true);

    @Autowired private CleanUp cleanUp;

    // Mock Bean
    @MockitoBean protected EmailService emailService;
    @MockitoBean protected BusinessStatusService businessStatusService;
    @MockitoBean protected DiscordNotificationService discordNotificationService;

    @BeforeAll
    public static void beforeAll(){
        POSTGRES_CONTAINER.start();
    }

    @AfterEach
    void tearDown() {
        cleanUp.all();
    }

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

        // PostgreSQL
        registry.add("spring.datasource.url",
                () -> "jdbc:postgresql://localhost:" + POSTGRES_CONTAINER.getMappedPort(5432) + "/swm");
        registry.add("spring.datasource.username", () -> "test");
        registry.add("spring.datasource.password", () -> "test");
    }
}
