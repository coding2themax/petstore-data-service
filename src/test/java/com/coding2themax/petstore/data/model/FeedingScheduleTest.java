package com.coding2themax.petstore.data.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class FeedingScheduleTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void closeValidator() {
    factory.close();
  }

  private static Set<String> invalidFields(FeedingSchedule schedule) {
    return validator.validate(schedule).stream()
        .map(ConstraintViolation::getPropertyPath)
        .map(Object::toString)
        .collect(Collectors.toSet());
  }

  @Test
  void withIdsKeepsOtherFields() {
    FeedingSchedule schedule = new FeedingSchedule(null, null, LocalTime.NOON, "Oats", BigDecimal.TEN, "kg", "n");

    assertEquals(new FeedingSchedule(3L, 4L, LocalTime.NOON, "Oats", BigDecimal.TEN, "kg", "n"),
        schedule.withIds(3L, 4L));
  }

  @Test
  void validSchedulePassesValidation() {
    assertTrue(invalidFields(new FeedingSchedule(null, null, LocalTime.NOON, "Oats", new BigDecimal("0.25"), "kg",
        null)).isEmpty());
  }

  @Test
  void missingRequiredFieldsAreRejected() {
    assertEquals(Set.of("feedingTime", "feedType", "quantity", "unit"),
        invalidFields(new FeedingSchedule(null, null, null, "", null, " ", null)));
  }

  @Test
  void nonPositiveQuantityIsRejected() {
    assertEquals(Set.of("quantity"),
        invalidFields(new FeedingSchedule(null, null, LocalTime.NOON, "Oats", BigDecimal.ZERO, "kg", null)));
  }

  @Test
  void tooLongFieldsAreRejected() {
    assertEquals(Set.of("feedType", "unit", "notes"),
        invalidFields(new FeedingSchedule(null, null, LocalTime.NOON, "x".repeat(81), BigDecimal.ONE,
            "x".repeat(21), "x".repeat(256))));
  }
}
