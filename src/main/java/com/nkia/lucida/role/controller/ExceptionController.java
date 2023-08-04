package com.nkia.lucida.role.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
@Slf4j
public class ExceptionController {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Object> badRequestException(final RuntimeException ex) {
    log.warn("Runtime Exception : {}", ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> accessDeniedException(final AccessDeniedException ex) {
    log.warn("Access Denied Exception : {}", ex);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllException(final Exception ex) {
    log.error("Error : {}", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }
}

