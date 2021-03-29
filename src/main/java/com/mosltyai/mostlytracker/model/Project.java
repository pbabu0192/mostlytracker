package com.mosltyai.mostlytracker.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "project")
@DynamicUpdate
public class Project extends Versioned {

  @NonNull @NotNull private String name;

  @NonNull @NotNull private LocalDate startDate;

  private LocalDate endDate;
}
