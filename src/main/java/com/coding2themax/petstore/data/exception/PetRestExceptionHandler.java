package com.coding2themax.petstore.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class PetRestExceptionHandler {
  @ExceptionHandler(StatusNotFoundException.class)
  ResponseEntity<String> handleStatusNotFoundException(StatusNotFoundException ex) {

    return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }
}
