package com.coding2themax.petstore.data.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class PetStatusConstraintValidatorTest {

  private final PetStatusConstraintValidator validator = new PetStatusConstraintValidator();

  @Test
  void acceptsEveryKnownStatus() {
    assertTrue(validator.isValid(List.of("available", "pending", "sold"), null));
  }

  @Test
  void acceptsEmptyList() {
    assertTrue(validator.isValid(List.of(), null));
  }

  @Test
  void rejectsUnknownStatus() {
    assertFalse(validator.isValid(List.of("available", "lost"), null));
  }

  @Test
  void isCaseSensitive() {
    assertFalse(validator.isValid(List.of("SOLD"), null));
  }
}
