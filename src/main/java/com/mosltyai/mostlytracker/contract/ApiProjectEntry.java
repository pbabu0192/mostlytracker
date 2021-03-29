package com.mosltyai.mostlytracker.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ApiProjectEntry {

  @ApiModelProperty(hidden = true)
  @JsonProperty("id")
  private UUID id;

  @ApiModelProperty(hidden = true)
  @JsonProperty("projectId")
  private UUID projectId;

  @JsonProperty("entryDate")
  private LocalDate entryDate;

  @DecimalMin(value = "0.01", message = "Time spent cannot be less that 0.01 hrs")
  @DecimalMax(value = "10.00", message = "Time spent cannot be greater that 10.00 hrs")
  @JsonProperty("timeSpent")
  private BigDecimal timeSpent;

  @JsonProperty("description")
  private String description;
}
