package com.mosltyai.mostlytracker.service;

import com.mosltyai.mostlytracker.model.Project;
import com.mosltyai.mostlytracker.model.ProjectEntry;
import com.mosltyai.mostlytracker.exception.ExceptionType;
import com.mosltyai.mostlytracker.exception.MostlyTrackerException;
import com.mosltyai.mostlytracker.mapper.ProjectMapper;
import com.mosltyai.mostlytracker.repository.ProjectEntryRepository;
import com.mosltyai.mostlytracker.repository.ProjectRepository;
import com.mosltyai.mostlytracker.service.type.ServiceProject;
import com.mosltyai.mostlytracker.service.type.ServiceProjectSummary;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@Slf4j
public class DefaultProjectService implements ProjectService {

  @NonNull private final ProjectRepository projectRepository;
  @NonNull private final ProjectEntryRepository projectEntryRepository;
  @NonNull private final ProjectMapper projectMapper;
  @NonNull private final BeanUtilsBean beanUtilsBean;

  @Override
  public Project createProject(final ServiceProject serviceProject) {
    validateDateRange(serviceProject);
    return projectRepository.save(projectMapper.toModel(serviceProject));
  }

  @Override
  public List<Project> getProjects() {
    return projectRepository.findAll();
  }

  @Override
  public Project getProjectById(final UUID projectId) {
    return projectRepository
        .findById(projectId)
        .orElseThrow(
            () ->
                new MostlyTrackerException(
                    ExceptionType.NOT_FOUND,
                    String.format("Project with id = %s is not found", projectId)));
  }

  @SneakyThrows
  @Override
  public Project updateProjectById(final ServiceProject serviceProject) {
    Project project =
        projectRepository
            .findById(serviceProject.getId())
            .orElseThrow(
                () ->
                    new MostlyTrackerException(
                        ExceptionType.NOT_FOUND,
                        String.format(
                            "Project with id = %s is not found", serviceProject.getId())));

    validateDateRange(serviceProject, project);

    beanUtilsBean.copyProperties(project, serviceProject);

    // updating endDate as it is as we might make this null intentionally.
    project.setEndDate(serviceProject.getEndDate());

    return projectRepository.save(project);
  }

  @Override
  public void deleteProjectById(final UUID projectId) {

    try {
      projectRepository.deleteById(projectId);
    } catch (EmptyResultDataAccessException ex) {
      throw new MostlyTrackerException(
          ExceptionType.DELETE_ERROR,
          String.format("Project with id = %s is not found", projectId));
    }
  }

  @Override
  public ServiceProjectSummary getProjectSummary(final UUID projectId) {

    // fetch project
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(
                () ->
                    new MostlyTrackerException(
                        ExceptionType.NOT_FOUND,
                        String.format("No project with id = %s is found", projectId)));

    // fetch project entries
    List<ProjectEntry> projectEntriesList = projectEntryRepository.findAllByProjectId(projectId);

    // total time spent for the project
    Double totalTimeSpent =
        projectEntriesList.stream()
            .map(ProjectEntry::getTimeSpent)
            .map(BigDecimal::doubleValue)
            .collect(Collectors.summingDouble(d -> d));

    Map<LocalDate, Double> groupedByEntryDate =
        projectEntriesList.stream()
            .collect(
                Collectors.groupingBy(
                    ProjectEntry::getEntryDate,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        projectEntries ->
                            projectEntries.stream()
                                .map(ProjectEntry::getTimeSpent)
                                .map(BigDecimal::doubleValue)
                                .collect(Collectors.summingDouble(d -> d)))));

    // prepare response
    ServiceProjectSummary.ServiceProjectSummaryBuilder builder =
        ServiceProjectSummary.builder()
            .id(projectId)
            .name(project.getName())
            .totalTimeSpent(BigDecimal.valueOf(totalTimeSpent));

    // set totalDays & averageTimeSpentPerDay
    Optional.ofNullable(groupedByEntryDate)
        .ifPresentOrElse(
            s -> {
              builder.totalDays((long) s.size());

              Double averageTimeSpentPerDay =
                  groupedByEntryDate.entrySet().stream()
                      .map(Map.Entry::getValue)
                      .collect(Collectors.averagingDouble(d -> d));
              builder.averageTimeSpentPerDay(averageTimeSpentPerDay);
            },
            () -> builder.totalDays(0L).averageTimeSpentPerDay(0.00));

    return builder.build();
  }

  /**
   * @param serviceProject
   * @param project
   */
  private void validateDateRange(final ServiceProject serviceProject, Project project) {
    LocalDate startDateToUpdate = serviceProject.getStartDate();
    LocalDate endDateToUpdate = serviceProject.getEndDate();
    LocalDate existingStartDate = project.getStartDate();
    LocalDate existingEndDate = project.getEndDate();

    if (Objects.nonNull(existingEndDate) && Objects.nonNull(startDateToUpdate)) {
      if (Objects.nonNull(endDateToUpdate)) existingEndDate = endDateToUpdate;
      if (!startDateToUpdate.isEqual(existingEndDate)
          && !startDateToUpdate.isBefore(existingEndDate)) {
        throw new MostlyTrackerException(
            ExceptionType.BAD_REQUEST,
            String.format(
                "StartDate = %s cannot be after endDate = %s", startDateToUpdate, existingEndDate));
      }
    }

    if (Objects.nonNull(existingStartDate) && Objects.nonNull(endDateToUpdate)) {
      if (Objects.nonNull(startDateToUpdate)) existingStartDate = startDateToUpdate;
      if (!endDateToUpdate.isEqual(existingStartDate)
          && !endDateToUpdate.isAfter(existingStartDate)) {
        throw new MostlyTrackerException(
            ExceptionType.BAD_REQUEST,
            String.format(
                "EndDate = %s cannot be before startDate = %s",
                endDateToUpdate, existingStartDate));
      }
    }
  }

  public void validateDateRange(ServiceProject serviceProject) {
    if (Objects.nonNull(serviceProject.getEndDate())
        && serviceProject.getStartDate().isAfter(serviceProject.getEndDate())) {
      throw new MostlyTrackerException(
          ExceptionType.BAD_REQUEST,
          String.format(
              "StartDate = %s cannot be after endDate = %s",
              serviceProject.getStartDate(), serviceProject.getEndDate()));
    }
  }
}
