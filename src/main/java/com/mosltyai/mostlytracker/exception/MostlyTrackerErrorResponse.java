package com.mosltyai.mostlytracker.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MostlyTrackerErrorResponse {

  private OffsetDateTime timestamp;
  private String status;
  private String message;
  private int code;
}
