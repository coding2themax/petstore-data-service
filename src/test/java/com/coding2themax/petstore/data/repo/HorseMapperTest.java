package com.coding2themax.petstore.data.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

class HorseMapperTest {

  private static Map<String, Object> row(Object horseId, Object scheduleId, String feedType, LocalTime time) {
    Map<String, Object> row = new HashMap<>();
    row.put("horseid", horseId);
    row.put("name", "Thunder");
    row.put("breed", "Thoroughbred");
    row.put("age", 7);
    row.put("owner", "Alex");
    row.put("scheduleid", scheduleId);
    row.put("feedtime", time);
    row.put("feedtype", feedType);
    row.put("quantity", scheduleId == null ? null : new BigDecimal("5.00"));
    row.put("unit", scheduleId == null ? null : "kg");
    row.put("notes", null);
    return row;
  }

  @Test
  void mapsHorseWithSchedules() {
    Horse horse = HorseMapper.toHorseFromRows(List.of(
        row(1L, 10L, "Hay", LocalTime.of(7, 0)),
        row(1L, 11L, "Oats", LocalTime.of(18, 0))));

    assertEquals(1L, horse.id());
    assertEquals("Thunder", horse.name());
    assertEquals("Thoroughbred", horse.breed());
    assertEquals(7, horse.age());
    assertEquals("Alex", horse.owner());
    assertEquals(List.of("Hay", "Oats"), horse.feedingSchedules().stream().map(FeedingSchedule::feedType).toList());
    assertEquals(10L, horse.feedingSchedules().get(0).id());
    assertEquals(1L, horse.feedingSchedules().get(0).horseId());
  }

  @Test
  void mapsHorseWithoutSchedulesFromLeftJoinRow() {
    Horse horse = HorseMapper.toHorseFromRows(List.of(row(2L, null, null, null)));

    assertEquals(2L, horse.id());
    assertTrue(horse.feedingSchedules().isEmpty());
  }

  @Test
  void acceptsAnyNumericIdType() {
    Horse horse = HorseMapper.toHorseFromRows(List.of(row(new BigDecimal("3"), 4, "Hay", LocalTime.NOON)));

    assertEquals(3L, horse.id());
    assertEquals(4L, horse.feedingSchedules().get(0).id());
  }

  @Test
  void mapsFeedingSchedule() {
    FeedingSchedule schedule = HorseMapper.toFeedingSchedule(row(1L, 10L, "Hay", LocalTime.of(7, 30)));

    assertEquals(new FeedingSchedule(10L, 1L, LocalTime.of(7, 30), "Hay", new BigDecimal("5.00"), "kg", null),
        schedule);
    assertNull(schedule.notes());
  }

  @Test
  void missingHorseIdFails() {
    assertThrows(NullPointerException.class, () -> HorseMapper.toHorseFromRows(List.of(row(null, null, null, null))));
  }
}
