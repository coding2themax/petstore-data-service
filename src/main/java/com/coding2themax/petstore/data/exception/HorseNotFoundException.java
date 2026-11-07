package com.coding2themax.petstore.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class HorseNotFoundException extends RuntimeException {

  public HorseNotFoundException(String message) {
    super(message);
  }
}
