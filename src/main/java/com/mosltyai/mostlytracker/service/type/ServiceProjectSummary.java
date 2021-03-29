package com.mosltyai.mostlytracker.service.type;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProjectSummary {

  @NonNull @NotNull private String name;

  @NonNull @NotNull private UUID id;

  @NonNull @NotNull private BigDecimal totalTimeSpent;

  @NonNull @NotNull private Long totalDays;

  @NonNull @NotNull private Double averageTimeSpentPerDay;
}
