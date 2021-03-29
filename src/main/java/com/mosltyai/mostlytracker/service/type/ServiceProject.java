package com.mosltyai.mostlytracker.service.type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProject {

  private UUID id;

  private String name;

  private LocalDate startDate;

  private LocalDate endDate;
}
