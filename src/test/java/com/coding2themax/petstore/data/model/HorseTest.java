package com.coding2themax.petstore.data.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class HorseTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  private final FeedingSchedule hay = new FeedingSchedule(null, null, LocalTime.of(7, 0), "Hay",
      BigDecimal.ONE, "kg", null);

  @BeforeAll
  static void setUpValidator() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void closeValidator() {
    factory.close();
  }

  private static Set<String> invalidFields(Object bean) {
    return validator.validate(bean).stream()
        .map(ConstraintViolation::getPropertyPath)
        .map(Object::toString)
        .collect(java.util.stream.Collectors.toSet());
  }

  @Test
  void nullSchedulesBecomeEmptyList() {
    assertEquals(List.of(), new Horse(1L, "Thunder", null, null, null, null).feedingSchedules());
  }

  @Test
  void schedulesAreCopiedAndImmutable() {
    List<FeedingSchedule> schedules = new ArrayList<>(List.of(hay));
    Horse horse = new Horse(1L, "Thunder", null, null, null, schedules);

    schedules.clear();

    assertEquals(1, horse.feedingSchedules().size());
    assertThrows(UnsupportedOperationException.class, () -> horse.feedingSchedules().add(hay));
  }

  @Test
  void withIdAndWithFeedingSchedulesKeepOtherFields() {
    Horse horse = new Horse(null, "Thunder", "Arabian", 4, "Jo", List.of());

    Horse changed = horse.withId(5L).withFeedingSchedules(List.of(hay));

    assertEquals(new Horse(5L, "Thunder", "Arabian", 4, "Jo", List.of(hay)), changed);
  }

  @Test
  void validHorsePassesValidation() {
    assertTrue(invalidFields(new Horse(null, "Thunder", "Arabian", 0, "Jo", List.of(hay))).isEmpty());
  }

  @Test
  void blankNameAndNegativeAgeAreRejected() {
    assertEquals(Set.of("name", "age"), invalidFields(new Horse(null, " ", null, -1, null, null)));
  }

  @Test
  void tooLongFieldsAreRejected() {
    String tooLong = "x".repeat(81);
    assertEquals(Set.of("name", "breed", "owner"),
        invalidFields(new Horse(null, tooLong, tooLong, null, tooLong, null)));
  }

  @Test
  void invalidNestedScheduleIsRejected() {
    FeedingSchedule bad = new FeedingSchedule(null, null, null, "Hay", BigDecimal.ONE, "kg", null);
    assertEquals(Set.of("feedingSchedules[0].feedingTime"),
        invalidFields(new Horse(null, "Thunder", null, null, null, List.of(bad))));
  }
}
