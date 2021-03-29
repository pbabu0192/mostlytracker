package com.mosltyai.mostlytracker.model;

import lombok.Data;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
@OptimisticLocking
@MappedSuperclass
public class Versioned {

  @Id
  @Column(updatable = false)
  private UUID id = UUID.randomUUID();

  @Version @ReadOnlyProperty private Long version;

  @Column(updatable = false)
  private OffsetDateTime created = OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);

  private OffsetDateTime updated;

  @PrePersist
  public void onPrePersist() {
    setUpdated(getCreated());
  }

  @PreUpdate
  public void onPreUpdate() {
    setUpdated(OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS));
  }
}
