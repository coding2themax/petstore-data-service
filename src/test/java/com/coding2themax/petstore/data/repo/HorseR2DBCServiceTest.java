package com.coding2themax.petstore.data.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

import reactor.test.StepVerifier;

@SpringBootTest
@Testcontainers
class HorseR2DBCServiceTest {

  @Autowired
  private HorseR2DBCService repository;

  private static FeedingSchedule schedule(int hour, String feedType, String notes) {
    return new FeedingSchedule(null, null, LocalTime.of(hour, 0), feedType, new BigDecimal("3.50"), "kg", notes);
  }

  @Test
  void findAllHorsesIncludesSeededHorses() {
    StepVerifier.create(repository.findAllHorses().collectList())
        .assertNext(horses -> {
          Horse thunder = horses.stream().filter(h -> h.id() == 1L).findFirst().orElseThrow();
          assertEquals("Thunder", thunder.name());
          assertEquals(2, thunder.feedingSchedules().size());
          assertTrue(horses.stream().anyMatch(h -> "Daisy".equals(h.name())));
        })
        .verifyComplete();
  }

  @Test
  void getHorseById() {
    StepVerifier.create(repository.getHorseById(2L))
        .assertNext(horse -> {
          assertEquals("Daisy", horse.name());
          assertEquals("Quarter Horse", horse.breed());
          assertEquals(12, horse.age());
          assertEquals(1, horse.feedingSchedules().size());
          assertEquals(LocalTime.of(8, 0), horse.feedingSchedules().get(0).feedingTime());
        })
        .verifyComplete();
  }

  @Test
  void getHorseByIdMissing() {
    StepVerifier.create(repository.getHorseById(9999L)).verifyComplete();
  }

  @Test
  void createReadUpdateAndDeleteHorse() {
    Horse newHorse = new Horse(null, "Comet", null, null, null,
        List.of(schedule(6, "Hay", "Soak first"), schedule(19, "Grain", null)));

    Horse created = repository.createHorse(newHorse).block();
    assertNotNull(created);
    assertNotNull(created.id());
    assertEquals(2, created.feedingSchedules().size());
    assertEquals(created.id(), created.feedingSchedules().get(0).horseId());

    StepVerifier.create(repository.getHorseById(created.id()))
        .assertNext(found -> {
          assertEquals("Comet", found.name());
          assertNull(found.breed());
          assertEquals(List.of("Hay", "Grain"), found.feedingSchedules().stream().map(FeedingSchedule::feedType).toList());
        })
        .verifyComplete();

    Horse changed = new Horse(null, "Comet", "Arabian", 4, "Jo", List.of(schedule(12, "Carrots", null)));
    StepVerifier.create(repository.updateHorse(created.id(), changed))
        .assertNext(updated -> {
          assertEquals("Arabian", updated.breed());
          assertEquals(1, updated.feedingSchedules().size());
        })
        .verifyComplete();
    StepVerifier.create(repository.getFeedingSchedules(created.id()).map(FeedingSchedule::feedType))
        .expectNext("Carrots")
        .verifyComplete();

    StepVerifier.create(repository.deleteHorse(created.id())).expectNext(true).verifyComplete();
    StepVerifier.create(repository.getHorseById(created.id())).verifyComplete();
    StepVerifier.create(repository.getFeedingSchedules(created.id())).verifyComplete();
  }

  @Test
  void updateMissingHorseIsEmpty() {
    StepVerifier.create(repository.updateHorse(9999L, new Horse(null, "Ghost", null, null, null, null)))
        .verifyComplete();
  }

  @Test
  void deleteMissingHorseReturnsFalse() {
    StepVerifier.create(repository.deleteHorse(9999L)).expectNext(false).verifyComplete();
  }

  @Test
  void addAndDeleteFeedingSchedule() {
    Horse horse = repository.createHorse(new Horse(null, "Pepper", null, null, null, null)).block();
    assertNotNull(horse);
    assertTrue(horse.feedingSchedules().isEmpty());

    FeedingSchedule added = repository.addFeedingSchedule(horse.id(), schedule(9, "Hay", null)).block();
    assertNotNull(added);
    assertNotNull(added.id());
    assertEquals(horse.id(), added.horseId());

    StepVerifier.create(repository.getFeedingSchedules(horse.id())).expectNext(added).verifyComplete();

    StepVerifier.create(repository.deleteFeedingSchedule(horse.id(), added.id())).expectNext(true).verifyComplete();
    StepVerifier.create(repository.deleteFeedingSchedule(horse.id(), added.id())).expectNext(false).verifyComplete();
    StepVerifier.create(repository.getFeedingSchedules(horse.id())).verifyComplete();
  }
}
