package com.coding2themax.petstore.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Slf4j
public class StatusNotFoundException extends RuntimeException {

  public StatusNotFoundException(String message) {
    super(message);
    log.error(message);

  }

}
