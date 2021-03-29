package com.mosltyai.mostlytracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.mosltyai.mostlytracker.MostlyTrackerApplicationTests;
import com.mosltyai.mostlytracker.contract.ApiProject;
import com.mosltyai.mostlytracker.model.Project;
import com.mosltyai.mostlytracker.exception.MostlyTrackerException;
import com.mosltyai.mostlytracker.mapper.ProjectMapper;
import com.mosltyai.mostlytracker.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
// @Disabled
class ProjectControllerTest extends MostlyTrackerApplicationTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private ProjectService projectService;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ProjectMapper projectMapper;

  @Value("${tracker.security.username}")
  private String username;

  @Value("${tracker.security.password}")
  private String password;

  @Test
  void testCreateProject_unauthorized() throws Exception {
    mockMvc.perform(post("/project")).andExpect(status().isUnauthorized()).andReturn();
  }

  @Test
  void testGetProject_unauthorized() throws Exception {
    mockMvc
        .perform(get("/project/" + UUID.randomUUID().toString()))
        .andExpect(status().isUnauthorized())
        .andReturn();
  }

  @Test
  void testGetAllProjects_unauthorized() throws Exception {
    mockMvc.perform(get("/project")).andExpect(status().isUnauthorized()).andReturn();
  }

  @Test
  void testUpdateProject_unauthorized() throws Exception {
    mockMvc
        .perform(patch("/project/" + UUID.randomUUID().toString()))
        .andExpect(status().isUnauthorized())
        .andReturn();
  }

  @Test
  void testDeleteProject_unauthorized() throws Exception {
    mockMvc
        .perform(delete("/project/" + UUID.randomUUID().toString()))
        .andExpect(status().isUnauthorized())
        .andReturn();
  }

  @Test
  void testCreateProject_success() throws Exception {

    ApiProject request = buildApiProject();

    mockMvc
        .perform(
            post("/project")
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.id").exists())
        .andReturn();
  }

  @Test
  void testCreateProject_badrequest() throws Exception {

    mockMvc
        .perform(
            post("/project")
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("")))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void testGetProject_success() throws Exception {

    ApiProject request = buildApiProject();

    // add project
    Project res = projectService.createProject(projectMapper.toService(request));

    mockMvc
        .perform(
            get("/project/" + res.getId())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(res.getId().toString()))
        .andReturn();
  }

  @Test
  void testGetProject_notfound() throws Exception {

    mockMvc
        .perform(
            get("/project/" + UUID.randomUUID().toString())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  void testGetAllProject_success() throws Exception {

    // add projects
    projectService.createProject(projectMapper.toService(buildApiProject()));
    projectService.createProject(projectMapper.toService(buildApiProject()));

    mockMvc
        .perform(
            get("/project")
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").exists())
        .andExpect(jsonPath("$.[1].id").exists())
        .andReturn();
  }

  @Test
  void testGetAllProject_empty() throws Exception {

    mockMvc
        .perform(
            get("/project")
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").doesNotExist())
        .andReturn();
  }

  @Test
  void testUpdateProject_success() throws Exception {

    // add project
    Project res = projectService.createProject(projectMapper.toService(buildApiProject()));
    ApiProject request = projectMapper.toApi(res);

    // update name and startdate
    request.setName("New proj");
    request.setStartDate(LocalDate.of(2021, 5, 20));

    mockMvc
        .perform(
            patch("/project/" + res.getId())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New proj"))
        .andExpect(jsonPath("$.startDate").value("2021-05-20"))
        .andReturn();
  }

  @Test
  void testUpdateProject_badrequest() throws Exception {

    mockMvc
        .perform(
            patch("/project/" + UUID.randomUUID())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("")))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void testDeleteProject_notfound() throws Exception {

    mockMvc
        .perform(
            delete("/project/" + UUID.randomUUID().toString())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotModified())
        .andReturn();
  }

  @Test
  void testDeleteProject_success() throws Exception {

    // add project
    Project res = projectService.createProject(projectMapper.toService(buildApiProject()));

    mockMvc
        .perform(
            delete("/project/" + res.getId())
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    Assertions.assertThrows(
        MostlyTrackerException.class, () -> projectService.getProjectById(res.getId()));
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
