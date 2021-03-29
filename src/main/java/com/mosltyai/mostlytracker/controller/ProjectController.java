package com.mosltyai.mostlytracker.controller;

import com.mosltyai.mostlytracker.contract.ApiProject;
import com.mosltyai.mostlytracker.contract.ApiProjectSummary;
import com.mosltyai.mostlytracker.mapper.ProjectMapper;
import com.mosltyai.mostlytracker.service.ProjectService;
import io.swagger.annotations.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Api(tags = "Project controller")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@RequestMapping("/project")
public class ProjectController {

  @NonNull private final ProjectService projectService;
  @NonNull private final ProjectMapper projectMapper;

  @ApiResponses({
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 201, message = "Project created "),
    @ApiResponse(code = 500, message = "Internal Server Error")
  })
  @ApiOperation(
      value = "Creates a new project",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PostMapping
  public ResponseEntity<ApiProject> createProject(@RequestBody ApiProject apiProject) {
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(
            projectMapper.toApi(projectService.createProject(projectMapper.toService(apiProject))));
  }

  @ApiResponses({
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 200, message = "Ok"),
    @ApiResponse(code = 500, message = "Internal Server Error")
  })
  @ApiOperation(
      value = "Get all the projects",
      produces = MediaType.APPLICATION_JSON_VALUE,
      authorizations = {@Authorization(value = "basicAuth")})
  @GetMapping
  public ResponseEntity<List<ApiProject>> getProjects() {
    return ResponseEntity.status(HttpStatus.OK)
        .body(projectMapper.toApi(projectService.getProjects()));
  }

  @ApiResponses({
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 200, message = "Ok"),
    @ApiResponse(code = 500, message = "Internal Server Error"),
    @ApiResponse(code = 404, message = "Not found")
  })
  @ApiOperation(
      value = "Get a project with project_id",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @GetMapping("/{projectId}")
  public ResponseEntity<ApiProject> getProjectById(@PathVariable("projectId") UUID projectId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(projectMapper.toApi(projectService.getProjectById(projectId)));
  }

  @ApiResponses({
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 200, message = "Project updated"),
    @ApiResponse(code = 500, message = "Internal Server Error"),
    @ApiResponse(code = 404, message = "Not found")
  })
  @ApiOperation(
      value = "Updates a project",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PatchMapping("/{projectId}")
  public ResponseEntity<ApiProject> updateProjectById(
      @RequestBody ApiProject apiProject, @Valid @NotNull @PathVariable UUID projectId) {
    return ResponseEntity.ok(
        projectMapper.toApi(
            projectService.updateProjectById(
                projectMapper.toServiceUpdate(apiProject, projectId))));
  }

  @ApiResponses({
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 200, message = "Project deleted"),
    @ApiResponse(code = 500, message = "Internal Server Error"),
    @ApiResponse(code = 404, message = "Not found"),
  })
  @ApiOperation(
      value = "Deletes a project with project_id",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @DeleteMapping("/{projectId}")
  public ResponseEntity<Void> deleteProject(@PathVariable("projectId") UUID projectId) {
    projectService.deleteProjectById(projectId);
    return ResponseEntity.ok().build();
  }

  @ApiResponses({
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 200, message = "Ok"),
    @ApiResponse(code = 500, message = "Internal Server Error"),
    @ApiResponse(code = 404, message = "Not found")
  })
  @ApiOperation(value = "Get summary of a project", produces = MediaType.APPLICATION_JSON_VALUE)
  @GetMapping("/summary/{projectId}")
  public ResponseEntity<ApiProjectSummary> getProjectSummary(
      @Valid @NotNull @PathVariable UUID projectId) {
    return ResponseEntity.ok()
        .body(projectMapper.toApi(projectService.getProjectSummary(projectId)));
  }
}
