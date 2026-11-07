package com.coding2themax.petstore.data.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * A horse and its feeding schedule.
 */
public record Horse(
    Long id,
    @NotBlank @Size(max = 80) String name,
    @Size(max = 80) String breed,
    @PositiveOrZero Integer age,
    @Size(max = 80) String owner,
    @Valid List<FeedingSchedule> feedingSchedules) {

  public Horse {
    feedingSchedules = feedingSchedules == null ? List.of() : List.copyOf(feedingSchedules);
  }

  public Horse withId(Long newId) {
    return new Horse(newId, name, breed, age, owner, feedingSchedules);
  }

  public Horse withFeedingSchedules(List<FeedingSchedule> schedules) {
    return new Horse(id, name, breed, age, owner, schedules);
  }
}
