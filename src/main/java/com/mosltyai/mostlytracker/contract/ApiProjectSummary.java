package com.mosltyai.mostlytracker.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ApiProjectSummary {

  @JsonProperty("name")
  private String name;

  @JsonProperty("id")
  private UUID id;

  @JsonProperty("totalTimeSpent")
  private BigDecimal totalTimeSpent;

  @JsonProperty("totalDays")
  private Long totalDays;

  @JsonProperty("averageTimeSpentPerDay")
  private Double averageTimeSpentPerDay;
}
