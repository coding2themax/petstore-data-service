package com.coding2themax.petstore.data.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Slf4j
public class TagNotFoundException extends RuntimeException {
  public TagNotFoundException(String message) {
    super(message);
    log.error(message);

  }
}
