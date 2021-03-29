package com.mosltyai.mostlytracker.service;

import com.mosltyai.mostlytracker.model.ProjectEntry;
import com.mosltyai.mostlytracker.service.type.ServiceProjectEntry;

import java.util.List;
import java.util.UUID;

public interface ProjectEntryService {

  ProjectEntry createProjectEntry(ServiceProjectEntry serviceProjectEntry);

  void deleteProjectEntry(UUID projectEntryId);

  List<ProjectEntry> getProjectEntriesByProject(UUID projectId);
}
