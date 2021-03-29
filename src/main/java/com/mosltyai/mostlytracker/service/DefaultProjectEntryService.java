package com.mosltyai.mostlytracker.service;

import com.mosltyai.mostlytracker.exception.ExceptionType;
import com.mosltyai.mostlytracker.exception.MostlyTrackerException;
import com.mosltyai.mostlytracker.mapper.ProjectEntryMapper;
import com.mosltyai.mostlytracker.model.Project;
import com.mosltyai.mostlytracker.model.ProjectEntry;
import com.mosltyai.mostlytracker.repository.ProjectEntryRepository;
import com.mosltyai.mostlytracker.repository.ProjectRepository;
import com.mosltyai.mostlytracker.service.type.ServiceProjectEntry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@Slf4j
public class DefaultProjectEntryService implements ProjectEntryService {
  @NonNull private final ProjectEntryRepository projectEntryRepository;
  @NonNull private final ProjectRepository projectRepository;
  @NonNull private final ProjectEntryMapper projectEntryMapper;

  @Value("${tracker.time-spent.allowed.per-day}")
  private Double allowedTimeSpentPerDay;

  /**
   * Creates a project entry for the given project
   *
   * @param serviceProjectEntry
   * @return
   */
  @Override
  public ProjectEntry createProjectEntry(final ServiceProjectEntry serviceProjectEntry) {

    boolean isValidProjectEntry =
        projectRepository
            .findById(serviceProjectEntry.getProjectId())
            .filter(project -> isValidProjectEntry(project, serviceProjectEntry))
            .isPresent();

    if (isValidProjectEntry) {
      return projectEntryRepository.save(projectEntryMapper.toModel(serviceProjectEntry));
    } else {
      throw new MostlyTrackerException(
          ExceptionType.NOT_FOUND,
          String.format(
              "Project with id = %s does not exists", serviceProjectEntry.getProjectId()));
    }
  }

  /**
   * Deletes a project entry with the project entry id
   *
   * @param projectEntryId
   */
  @Override
  public void deleteProjectEntry(final UUID projectEntryId) {
    try {
      projectEntryRepository.deleteById(projectEntryId);
    } catch (EmptyResultDataAccessException ex) {
      throw new MostlyTrackerException(
          ExceptionType.DELETE_ERROR,
          String.format("Project entry with id = %s is not found", projectEntryId));
    }
  }

  /**
   * Retrieve all project entries for the given project id
   *
   * @param projectId
   * @return
   */
  @Override
  public List<ProjectEntry> getProjectEntriesByProject(final UUID projectId) {
    return projectEntryRepository.findAllByProjectId(projectId).stream()
        .sorted(Comparator.comparing(ProjectEntry::getEntryDate))
        .collect(Collectors.toList());
  }

  private boolean isValidProjectEntry(
      final Project project, final ServiceProjectEntry serviceProjectEntry) {

    boolean isValid = true;
    StringBuilder errorMessage =
        new StringBuilder(
            String.format("Project entry date = %s ", serviceProjectEntry.getEntryDate()));

    // check if project entry date is equal to/after project start date
    if (!serviceProjectEntry.getEntryDate().isEqual(project.getStartDate())
        && serviceProjectEntry.getEntryDate().isBefore(project.getStartDate())) {
      isValid = false;
      errorMessage.append(String.format("must be after =  %s ", project.getStartDate()));
    }

    // check if project entry date is equal to/before project end date
    if (Objects.nonNull(project.getEndDate())
        && !serviceProjectEntry.getEntryDate().isEqual(project.getEndDate())
        && serviceProjectEntry.getEntryDate().isAfter(project.getEndDate())) {
      isValid = false;
      errorMessage.append(String.format("must be before =  %s", project.getEndDate()));
    }

    if (!isValid) {
      throw new MostlyTrackerException(ExceptionType.BAD_REQUEST, errorMessage.toString());
    }

    Double actaulTimeSpent = getTimeSpentPerDay(serviceProjectEntry);
    if (actaulTimeSpent + serviceProjectEntry.getTimeSpent().doubleValue()
        > allowedTimeSpentPerDay) {
      throw new MostlyTrackerException(
          ExceptionType.BAD_REQUEST,
          String.format("Exceeds allowed time spent per day = %s", allowedTimeSpentPerDay));
    }

    return true;
  }

  private Double getTimeSpentPerDay(ServiceProjectEntry serviceProjectEntry) {

    return projectEntryRepository
        .findAllByEntryDateAndProjectId(
            serviceProjectEntry.getEntryDate(), serviceProjectEntry.getProjectId())
        .stream()
        .map(ProjectEntry::getTimeSpent)
        .map(BigDecimal::doubleValue)
        .collect(Collectors.summingDouble(d -> d));
  }
}
