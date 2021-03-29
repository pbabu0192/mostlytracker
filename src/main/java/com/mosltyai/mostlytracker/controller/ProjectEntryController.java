package com.mosltyai.mostlytracker.controller;

import com.mosltyai.mostlytracker.contract.ApiProjectEntry;
import com.mosltyai.mostlytracker.mapper.ProjectEntryMapper;
import com.mosltyai.mostlytracker.service.ProjectEntryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Api(tags = "Project Entry Controller")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@RequestMapping("/project/entry")
public class ProjectEntryController {

  @NonNull private final ProjectEntryService projectEntryService;
  @NonNull private final ProjectEntryMapper projectEntryMapper;

  @ApiResponses({
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 201, message = "ProjectEntry created "),
    @ApiResponse(code = 500, message = "Internal Server Error")
  })
  @ApiOperation(
      value = "Creates a new project entry",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @PostMapping("/{projectId}")
  public ResponseEntity<ApiProjectEntry> createProjectEntry(
      @Valid @RequestBody ApiProjectEntry apiProjectEntry,
      @Valid @NotNull @PathVariable UUID projectId) {

    return ResponseEntity.accepted()
        .body(
            projectEntryMapper.toApi(
                projectEntryService.createProjectEntry(
                    projectEntryMapper.toService(apiProjectEntry, projectId))));
  }

  @ApiResponses({
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 200, message = "ProjectEntry deleted "),
    @ApiResponse(code = 500, message = "Internal Server Error"),
    @ApiResponse(code = 404, message = "Not found")
  })
  @ApiOperation(
      value = "Deletes a new project entry with project entry id",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @DeleteMapping("/{projectEntryId}")
  public ResponseEntity<Void> deleteProjectEntry(
      @Valid @NotNull @PathVariable UUID projectEntryId) {

    projectEntryService.deleteProjectEntry(projectEntryId);

    return ResponseEntity.ok().build();
  }

  @ApiResponses({
    @ApiResponse(code = 400, message = "Bad request"),
    @ApiResponse(code = 200, message = "Ok"),
    @ApiResponse(code = 500, message = "Internal Server Error"),
    @ApiResponse(code = 404, message = "Not found")
  })
  @ApiOperation(
      value = "Get all project entries of a project",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @GetMapping("/{projectId}")
  public ResponseEntity<List<ApiProjectEntry>> getAllProjectEntriesByProjectId(
      @Valid @NotNull @PathVariable UUID projectId) {

    return ResponseEntity.ok()
        .body(projectEntryMapper.toApi(projectEntryService.getProjectEntriesByProject(projectId)));
  }
}
