package com.coding2themax.petstore.data.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class StatusNotFoundException extends RuntimeException {

  private static final Logger log = LoggerFactory.getLogger(StatusNotFoundException.class);

  public StatusNotFoundException(String message) {
    super(message);
    log.error(message);

  }

}
