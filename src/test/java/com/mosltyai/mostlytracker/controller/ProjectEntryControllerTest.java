package com.mosltyai.mostlytracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.mosltyai.mostlytracker.MostlyTrackerApplicationTests;
import com.mosltyai.mostlytracker.contract.ApiProject;
import com.mosltyai.mostlytracker.contract.ApiProjectEntry;
import com.mosltyai.mostlytracker.model.Project;
import com.mosltyai.mostlytracker.model.ProjectEntry;
import com.mosltyai.mostlytracker.exception.MostlyTrackerException;
import com.mosltyai.mostlytracker.mapper.ProjectEntryMapper;
import com.mosltyai.mostlytracker.mapper.ProjectMapper;
import com.mosltyai.mostlytracker.service.ProjectEntryService;
import com.mosltyai.mostlytracker.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
class ProjectEntryControllerTest extends MostlyTrackerApplicationTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private ProjectEntryService projectEntryService;
  @Autowired private ProjectService projectService;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ProjectEntryMapper projectEntryMapper;
  @Autowired private ProjectMapper projectMapper;

  @Value("${tracker.security.username}")
  private String username;

  @Value("${tracker.security.password}")
  private String password;

  @Test
  void testCreateProjectEntry_unauthorized() throws Exception {
    mockMvc.perform(post("/project/entry")).andExpect(status().isUnauthorized()).andReturn();
  }

  @Test
  void testGetAllProjectEntries_unauthorized() throws Exception {
    mockMvc
        .perform(get("/project/entry/" + UUID.randomUUID().toString()))
        .andExpect(status().isUnauthorized())
        .andReturn();
  }

  @Test
  void testdeleteProjectEntry_unauthorized() throws Exception {
    mockMvc
        .perform(delete("/project/entry" + UUID.randomUUID().toString()))
        .andExpect(status().isUnauthorized())
        .andReturn();
  }

  @Test
  void testCreateProjectEntry_success() throws Exception {
    // add project
    Project project = projectService.createProject(projectMapper.toService(buildApiProject()));

    ApiProjectEntry request = buildApiProjectEntry(project.getId(), 3.09);

    mockMvc
        .perform(
            post("/project/entry/" + project.getId().toString())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.id").exists())
        .andReturn();
  }

  @Test
  void testCreateProjectEntry_notfound() throws Exception {
    // add project

    ApiProjectEntry request = buildApiProjectEntry(UUID.randomUUID(), 3.09);

    mockMvc
        .perform(
            post("/project/entry/" + UUID.randomUUID().toString())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  void testGetAllProjectEntry_success() throws Exception {
    // add project
    Project project = projectService.createProject(projectMapper.toService(buildApiProject()));

    projectEntryService.createProjectEntry(
        projectEntryMapper.toService(buildApiProjectEntry(project.getId(), 3.09), project.getId()));
    projectEntryService.createProjectEntry(
        projectEntryMapper.toService(buildApiProjectEntry(project.getId(), 5.89), project.getId()));

    mockMvc
        .perform(
            get("/project/entry/" + project.getId().toString())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").exists())
        .andExpect(jsonPath("$.[1].id").exists())
        .andReturn();
  }

  @Test
  void testGetAllProjectEntry_empty() throws Exception {

    mockMvc
        .perform(
            get("/project/entry/" + UUID.randomUUID().toString())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").doesNotExist())
        .andReturn();
  }

  @Test
  void testDeleteProjectEntry_success() throws Exception {
    // add project
    Project project = projectService.createProject(projectMapper.toService(buildApiProject()));

    ProjectEntry res =
        projectEntryService.createProjectEntry(
            projectEntryMapper.toService(
                buildApiProjectEntry(project.getId(), 4.09), project.getId()));

    mockMvc
        .perform(
            delete("/project/entry/" + res.getId().toString())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    Assertions.assertThrows(
        MostlyTrackerException.class, () -> projectEntryService.deleteProjectEntry(res.getId()));
  }

  @Test
  void testDeleteProjectEntry_notmodified() throws Exception {

    mockMvc
        .perform(
            delete("/project/entry/" + UUID.randomUUID().toString())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotModified())
        .andReturn();
  }

  private ApiProjectEntry buildApiProjectEntry(UUID projectId, Double timeSpent) {
    return ApiProjectEntry.builder()
        .projectId(projectId)
        .timeSpent(BigDecimal.valueOf(timeSpent))
        .entryDate(LocalDate.now())
        .description("Test project entry")
        .build();
  }

  private ApiProject buildApiProject() {

    Faker faker = Faker.instance();

    return ApiProject.builder()
        .name(faker.name().name())
        .endDate(LocalDate.now().plusMonths(3))
        .startDate(LocalDate.now())
        .build();
  }
}
