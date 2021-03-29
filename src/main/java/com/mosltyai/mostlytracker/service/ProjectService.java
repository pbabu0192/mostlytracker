package com.mosltyai.mostlytracker.service;

import com.mosltyai.mostlytracker.model.Project;
import com.mosltyai.mostlytracker.service.type.ServiceProject;
import com.mosltyai.mostlytracker.service.type.ServiceProjectSummary;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
  Project createProject(ServiceProject project);

  List<Project> getProjects();

  Project getProjectById(UUID projectId);

  Project updateProjectById(ServiceProject project);

  void deleteProjectById(UUID projectId);

  ServiceProjectSummary getProjectSummary(UUID projectId);
}
