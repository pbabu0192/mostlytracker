package com.mosltyai.mostlytracker.exception;

import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.util.Strings;

@Getter
@ToString(callSuper = true)
public class MostlyTrackerException extends RuntimeException {
  private final ExceptionType exceptionType;

  public MostlyTrackerException(ExceptionType exceptionType, String message) {
    super(Strings.isNotBlank(message) ? message : exceptionType.toString());
    this.exceptionType = exceptionType;
  }
}
