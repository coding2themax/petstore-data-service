package com.coding2themax.petstore.data.model;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * One feeding for a horse: when, what and how much.
 */
public record FeedingSchedule(
    Long id,
    Long horseId,
    @NotNull LocalTime feedingTime,
    @NotBlank @Size(max = 80) String feedType,
    @NotNull @Positive BigDecimal quantity,
    @NotBlank @Size(max = 20) String unit,
    @Size(max = 255) String notes) {

  public FeedingSchedule withIds(Long newId, Long newHorseId) {
    return new FeedingSchedule(newId, newHorseId, feedingTime, feedType, quantity, unit, notes);
  }
}
