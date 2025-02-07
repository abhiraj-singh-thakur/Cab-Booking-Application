package org.example.uber;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainerConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        DockerImageName image = DockerImageName.parse("postgis/postgis:12-3-0")
                .asCompatibleSubstituteFor("postgres");
        return new PostgreSQLContainer<>(image);
    }
}
