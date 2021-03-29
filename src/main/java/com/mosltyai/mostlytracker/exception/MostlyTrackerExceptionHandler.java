package com.mosltyai.mostlytracker.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@ControllerAdvice
@Slf4j
public class MostlyTrackerExceptionHandler {

  @Order(value = Ordered.HIGHEST_PRECEDENCE)
  @ExceptionHandler(Throwable.class)
  public @ResponseBody ResponseEntity<MostlyTrackerErrorResponse> handleMostlyTrackerException(
      Throwable ex) {

    MostlyTrackerErrorResponse.MostlyTrackerErrorResponseBuilder builder =
        MostlyTrackerErrorResponse.builder();

    HttpStatus status;
    if (ex instanceof MostlyTrackerException) {

      switch (((MostlyTrackerException) ex).getExceptionType()) {
        case CREATE_ERROR:
        default:
          status = HttpStatus.NOT_ACCEPTABLE;
          break;
        case UPDATE_ERROR:
        case DELETE_ERROR:
          status = HttpStatus.NOT_MODIFIED;
          break;
        case NOT_FOUND:
          status = HttpStatus.NOT_FOUND;
          break;
        case BAD_REQUEST:
          status = HttpStatus.BAD_REQUEST;
          break;
      }
    } else if (ex instanceof HttpMessageConversionException) {
      status = HttpStatus.BAD_REQUEST;
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    builder.message(ex.getMessage());
    builder.status(status.toString());
    builder.timestamp(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS));
    MostlyTrackerErrorResponse errorResponse = builder.build();
    errorResponse.setCode(status.value());

    return new ResponseEntity<>(errorResponse, status);
  }
}
