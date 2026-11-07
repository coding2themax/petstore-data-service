package com.coding2themax.petstore.data.repo;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

public final class HorseMapper {

  private HorseMapper() {
  }

  /** Builds a horse from the rows of a horse left-joined with its feeding schedules. */
  public static Horse toHorseFromRows(List<Map<String, Object>> rows) {
    Map<String, Object> first = rows.get(0);
    List<FeedingSchedule> schedules = rows.stream()
        .filter(r -> r.get("scheduleid") != null)
        .map(HorseMapper::toFeedingSchedule)
        .toList();
    return new Horse(
        toLong(first.get("horseid")),
        (String) first.get("name"),
        (String) first.get("breed"),
        (Integer) first.get("age"),
        (String) first.get("owner"),
        schedules);
  }

  public static FeedingSchedule toFeedingSchedule(Map<String, Object> row) {
    return new FeedingSchedule(
        toLong(row.get("scheduleid")),
        toLong(row.get("horseid")),
        (LocalTime) row.get("feedtime"),
        (String) row.get("feedtype"),
        (BigDecimal) row.get("quantity"),
        (String) row.get("unit"),
        (String) row.get("notes"));
  }

  private static Long toLong(Object value) {
    return ((Number) Objects.requireNonNull(value)).longValue();
  }
}
