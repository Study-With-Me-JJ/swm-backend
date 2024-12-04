package com.jj.swm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
class SwmBackendApplicationTests {
    static {
        GenericContainer redis = new GenericContainer(DockerImageName.parse("bitnami/valkey:8.0.1"))
                .withEnv("VALKEY_PASSWORD", "password")
                .withExposedPorts(6379);
        redis.start();
        System.setProperty("redis.host", redis.getHost());
        System.setProperty("redis.port", String.valueOf(redis.getMappedPort(6379)));
        System.setProperty("redis.password", "password");
    }

    @ServiceConnection
    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:17"));

    @Test
    void contextLoads() {
    }

}
