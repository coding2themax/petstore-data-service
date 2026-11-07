package com.coding2themax.petstore.data.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

class HorseItemTest {

  private static FeedingSchedule feeding(long id, int hour) {
    return new FeedingSchedule(id, 1L, LocalTime.of(hour, 0), "Hay", BigDecimal.ONE, "kg", "note");
  }

  @Test
  void roundTripsAHorseAndSortsFeedingsByTimeThenId() {
    Horse horse = new Horse(1L, "Thunder", "Thoroughbred", 7, "Alex",
        List.of(feeding(3, 18), feeding(2, 7), feeding(1, 7)));

    Horse read = HorseItem.from(horse).toHorse();

    assertEquals(List.of(1L, 2L, 3L), read.feedingSchedules().stream().map(FeedingSchedule::id).toList());
    assertEquals(horse.withFeedingSchedules(List.of(feeding(1, 7), feeding(2, 7), feeding(3, 18))), read);
  }

  @Test
  void feedingsTakeTheHorseIdOfTheItem() {
    HorseItem item = HorseItem.from(new Horse(1L, "Thunder", null, null, null, List.of(feeding(1, 7))));
    item.setHorseId(8L);

    assertEquals(8L, item.toFeedingSchedules().get(0).horseId());
  }

  @Test
  void addAndRemoveFeedings() {
    HorseItem item = new HorseItem();
    item.setHorseId(1L);
    item.setFeedingSchedules(null);

    assertEquals(List.of(), item.toFeedingSchedules());
    assertFalse(item.removeFeedingSchedule(1L));

    item.addFeedingSchedule(feeding(1, 7));
    item.addFeedingSchedule(feeding(2, 8));

    assertTrue(item.removeFeedingSchedule(1L));
    assertFalse(item.removeFeedingSchedule(1L));
    assertEquals(List.of(feeding(2, 8)), item.toFeedingSchedules());
  }

  @Test
  void settersAndGettersMatch() {
    HorseItem item = new HorseItem();
    item.setName("Daisy");
    item.setBreed("Quarter Horse");
    item.setAge(12);
    item.setOwner("Sam");
    item.setVersion(3L);

    assertEquals("Daisy", item.getName());
    assertEquals("Quarter Horse", item.getBreed());
    assertEquals(12, item.getAge());
    assertEquals("Sam", item.getOwner());
    assertEquals(3L, item.getVersion());
    assertEquals(List.of(), item.getFeedingSchedules());

    HorseItem.FeedingItem feeding = new HorseItem.FeedingItem();
    feeding.setId(1L);
    feeding.setFeedingTime(LocalTime.NOON);
    feeding.setFeedType("Oats");
    feeding.setQuantity(BigDecimal.TEN);
    feeding.setUnit("kg");
    feeding.setNotes("n");

    assertEquals(new FeedingSchedule(1L, 2L, LocalTime.NOON, "Oats", BigDecimal.TEN, "kg", "n"),
        feeding.toFeedingSchedule(2L));
    assertEquals(1L, feeding.getId());
    assertEquals(LocalTime.NOON, feeding.getFeedingTime());
    assertEquals("Oats", feeding.getFeedType());
    assertEquals(BigDecimal.TEN, feeding.getQuantity());
    assertEquals("kg", feeding.getUnit());
    assertEquals("n", feeding.getNotes());
  }
}
