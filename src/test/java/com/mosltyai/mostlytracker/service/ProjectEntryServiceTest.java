package com.mosltyai.mostlytracker.service;

import com.mosltyai.mostlytracker.MostlyTrackerApplicationTests;
import com.mosltyai.mostlytracker.model.Project;
import com.mosltyai.mostlytracker.exception.ExceptionType;
import com.mosltyai.mostlytracker.exception.MostlyTrackerException;
import com.mosltyai.mostlytracker.service.type.ServiceProject;
import com.mosltyai.mostlytracker.service.type.ServiceProjectEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class ProjectEntryServiceTest extends MostlyTrackerApplicationTests {

  @Autowired private ProjectEntryService projectEntryService;
  @Autowired private ProjectService projectService;

  @Test
  void createProjectEntry_timeSpentExceeded() {

    Project project =
        projectService.createProject(
            ServiceProject.builder()
                .name("test")
                .startDate(LocalDate.of(2021, 05, 06))
                .endDate(LocalDate.of(2021, 8, 21))
                .build());

    projectEntryService.createProjectEntry(
        ServiceProjectEntry.builder()
            .projectId(project.getId())
            .timeSpent(BigDecimal.valueOf(8.90))
            .entryDate(LocalDate.of(2021, 5, 8))
            .description("entry 1")
            .build());

    // create another entry for the same day which will exceed the allowedTimeSpentPerDay
    ServiceProjectEntry request =
        ServiceProjectEntry.builder()
            .projectId(project.getId())
            .timeSpent(BigDecimal.valueOf(8.90))
            .entryDate(LocalDate.of(2021, 5, 8))
            .description("entry 1")
            .build();

    MostlyTrackerException exception =
        Assertions.assertThrows(
            MostlyTrackerException.class, () -> projectEntryService.createProjectEntry(request));

    assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.BAD_REQUEST);
    assertThat(exception.getMessage()).isEqualTo("Exceeds allowed time spent per day = 10.0");
  }

  @Test
  void createProjectEntry_invalid_date_range() {

    Project project =
        projectService.createProject(
            ServiceProject.builder()
                .name("test")
                .startDate(LocalDate.of(2021, 05, 06))
                .endDate(LocalDate.of(2021, 8, 21))
                .build());

    projectEntryService.createProjectEntry(
        ServiceProjectEntry.builder()
            .projectId(project.getId())
            .timeSpent(BigDecimal.valueOf(4.90))
            .entryDate(LocalDate.of(2021, 5, 8))
            .description("entry 1")
            .build());

    // create another entry for the same day which will exceed the allowedTimeSpentPerDay
    ServiceProjectEntry request =
        ServiceProjectEntry.builder()
            .projectId(project.getId())
            .timeSpent(BigDecimal.valueOf(3.33))
            .entryDate(LocalDate.of(2021, 10, 8))
            .description("entry 1")
            .build();

    MostlyTrackerException exception =
        Assertions.assertThrows(
            MostlyTrackerException.class, () -> projectEntryService.createProjectEntry(request));

    assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.BAD_REQUEST);
    assertThat(exception.getMessage())
        .isEqualTo("Project entry date = 2021-10-08 must be before =  2021-08-21");
  }
}
