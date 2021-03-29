package com.mosltyai.mostlytracker.service;

import com.github.javafaker.Faker;
import com.mosltyai.mostlytracker.MostlyTrackerApplicationTests;
import com.mosltyai.mostlytracker.model.Project;
import com.mosltyai.mostlytracker.exception.ExceptionType;
import com.mosltyai.mostlytracker.exception.MostlyTrackerException;
import com.mosltyai.mostlytracker.service.type.ServiceProject;
import com.mosltyai.mostlytracker.service.type.ServiceProjectEntry;
import com.mosltyai.mostlytracker.service.type.ServiceProjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Slf4j
class ProjectServiceTest extends MostlyTrackerApplicationTests {

  @Autowired private ProjectEntryService projectEntryService;
  @Autowired private ProjectService projectService;

  @Test
  void testUpdateService_only_update_non_null_fields() {

    // add project
    Project project = projectService.createProject(buildServiceProject(LocalDate.now(), null));

    // update only name and endDate(will be updated irrespective of it being null/non-null)
    Project updatedProject =
        projectService.updateProjectById(
            ServiceProject.builder()
                .id(project.getId())
                .name("New name")
                .startDate(null)
                .endDate(project.getStartDate().plusMonths(3))
                .build());

    assertThat(updatedProject.getId()).isEqualTo(project.getId());
    // startDate should not be updated as it is passed null
    assertThat(updatedProject.getStartDate()).isEqualTo(project.getStartDate());
    assertThat(updatedProject.getEndDate().isEqual(project.getStartDate().plusMonths(3)));
    assertThat(updatedProject.getName()).isEqualTo("New name");
  }

  @Test
  void createProject_invalid_daterange() {
    MostlyTrackerException exception =
        Assertions.assertThrows(
            MostlyTrackerException.class,
            () ->
                projectService.createProject(
                    buildServiceProject(LocalDate.now().plusMonths(2), LocalDate.now())));

    assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.BAD_REQUEST);
    assertThat(exception.getMessage())
        .isEqualTo("StartDate = 2021-05-29 cannot be after endDate = 2021-03-29");
  }

  @Test
  void updateProject_invalid_daterange() {

    // add project
    Project project = projectService.createProject(buildServiceProject(LocalDate.now(), null));

    ServiceProject request =
        ServiceProject.builder()
            .id(project.getId())
            .name("New name")
            .startDate(null)
            // invalid end date
            .endDate(project.getStartDate().minusMonths(3))
            .build();

    MostlyTrackerException exception =
        Assertions.assertThrows(
            MostlyTrackerException.class, () -> projectService.updateProjectById(request));

    assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.BAD_REQUEST);
    assertThat(exception.getMessage())
        .isEqualTo("EndDate = 2020-12-29 cannot be before startDate = 2021-03-29");
  }

  @Test
  void testGetProjectSummary_success() {

    // add project
    Project project =
        projectService.createProject(
            buildServiceProject(LocalDate.of(2021, 5, 20), LocalDate.of(2021, 7, 20)));

    // add project entries
    ServiceProjectEntry serviceProjectEntry1 =
        ServiceProjectEntry.builder()
            .projectId(project.getId())
            .entryDate(LocalDate.of(2021, 5, 21))
            .timeSpent(BigDecimal.valueOf(3.09))
            .description("Project entry 1")
            .build();

    ServiceProjectEntry serviceProjectEntry2 =
        ServiceProjectEntry.builder()
            .projectId(project.getId())
            .entryDate(LocalDate.of(2021, 5, 21))
            .timeSpent(BigDecimal.valueOf(4.89))
            .description("Project entry 1")
            .build();

    ServiceProjectEntry serviceProjectEntry3 =
        ServiceProjectEntry.builder()
            .projectId(project.getId())
            .entryDate(LocalDate.of(2021, 5, 22))
            .timeSpent(BigDecimal.valueOf(10.00))
            .description("Project entry 1")
            .build();

    projectEntryService.createProjectEntry(serviceProjectEntry1);
    projectEntryService.createProjectEntry(serviceProjectEntry2);
    projectEntryService.createProjectEntry(serviceProjectEntry3);

    ServiceProjectSummary summary = projectService.getProjectSummary(project.getId());

    assertThat(summary.getName()).isEqualTo(project.getName());
    assertThat(summary.getId()).isEqualTo(project.getId());
    assertThat(summary.getAverageTimeSpentPerDay()).isEqualTo(8.99);
    assertThat(summary.getTotalDays()).isEqualTo(2);
    assertThat(summary.getTotalTimeSpent()).isEqualTo(BigDecimal.valueOf(17.98));
  }

  private ServiceProject buildServiceProject(LocalDate startDate, LocalDate endDate) {
    Faker faker = Faker.instance();
    return ServiceProject.builder()
        .name(faker.name().name())
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }
}
