package com.mosltyai.mostlytracker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public abstract class MostlyTrackerApplicationTests {

  private static final PostgreSQLContainer PSQL_CONTAINER;

  static {
    PSQL_CONTAINER = new PostgreSQLContainer("postgres:11");
    PSQL_CONTAINER.start();
  }

  @DynamicPropertySource
  public static void setDataSourceProperties(
      final DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add(
        "spring.datasource.url",
        () -> PSQL_CONTAINER.getJdbcUrl().concat("&currentSchema=mostly-tracker"));
    dynamicPropertyRegistry.add("spring.datasource.password", PSQL_CONTAINER::getPassword);
    dynamicPropertyRegistry.add("spring.datasource.username", PSQL_CONTAINER::getUsername);
  }
}
