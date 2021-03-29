package com.mosltyai.mostlytracker.repository;

import com.mosltyai.mostlytracker.model.ProjectEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectEntryRepository extends JpaRepository<ProjectEntry, UUID> {

  List<ProjectEntry> findAllByProjectId(UUID projectId);

  List<ProjectEntry> findAllByEntryDateAndProjectId(LocalDate entryDate, UUID projectId);
}
