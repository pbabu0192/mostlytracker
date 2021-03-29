package com.mosltyai.mostlytracker.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectEntry extends Versioned {

  @NonNull
  @NotNull
  @JoinColumn(referencedColumnName = "id", table = "project")
  @Column(updatable = false)
  private UUID projectId;

  @NonNull @NotNull private LocalDate entryDate;

  @NonNull @NotNull private BigDecimal timeSpent;

  private String description;
}
