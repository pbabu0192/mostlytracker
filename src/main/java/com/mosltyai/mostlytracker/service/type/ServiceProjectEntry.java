package com.mosltyai.mostlytracker.service.type;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProjectEntry {

  @NonNull @NotNull private UUID projectId;

  @NonNull @NotNull private LocalDate entryDate;

  @NonNull @NotNull private BigDecimal timeSpent;

  private String description;
}
