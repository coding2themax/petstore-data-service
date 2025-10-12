package com.coding2themax.petstore.data.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TagNotFoundException extends RuntimeException {

  private static final Logger log = LoggerFactory.getLogger(TagNotFoundException.class);

  public TagNotFoundException(String message) {
    super(message);
    log.error(message);

  }
}
