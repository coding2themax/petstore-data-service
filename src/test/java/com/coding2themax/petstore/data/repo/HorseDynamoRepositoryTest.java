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

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;
import com.coding2themax.petstore.data.support.DynamoDbTest;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/** Integration test of the horse repository against DynamoDB Local. */
@DynamoDbTest
class HorseDynamoRepositoryTest {

  @Autowired
  private HorseDynamoRepository repository;

  private static FeedingSchedule schedule(int hour, String feedType, String notes) {
    return new FeedingSchedule(null, null, LocalTime.of(hour, 0), feedType, new BigDecimal("3.5"), "kg", notes);
  }

  private Horse create(String name, FeedingSchedule... schedules) {
    Horse horse = repository.createHorse(new Horse(null, name, null, null, null, List.of(schedules))).block();
    assertNotNull(horse);
    return horse;
  }

  @Test
  void findAllHorsesIncludesSampleHorses() {
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
          assertEquals(new FeedingSchedule(3L, 2L, LocalTime.of(8, 0), "Hay", new BigDecimal("4"), "kg", null),
              horse.feedingSchedules().get(0));
        })
        .verifyComplete();
  }

  @Test
  void getHorseByIdMissing() {
    StepVerifier.create(repository.getHorseById(9999L)).verifyComplete();
  }

  @Test
  void createReadUpdateAndDeleteHorse() {
    Horse created = create("Comet", schedule(19, "Grain", null), schedule(6, "Hay", "Soak first"));
    assertTrue(created.id() > 2, "new ids continue after the sample data");
    assertEquals(created.id(), created.feedingSchedules().get(0).horseId());
    assertTrue(created.feedingSchedules().stream().allMatch(s -> s.id() != null));

    StepVerifier.create(repository.getHorseById(created.id()))
        .assertNext(found -> {
          assertEquals("Comet", found.name());
          assertNull(found.breed());
          // stored feedings come back in time order
          assertEquals(List.of("Hay", "Grain"), found.feedingSchedules().stream().map(FeedingSchedule::feedType).toList());
          assertEquals("Soak first", found.feedingSchedules().get(0).notes());
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
  void updateMissingHorseIsEmptyAndDoesNotCreateIt() {
    StepVerifier.create(repository.updateHorse(9999L, new Horse(null, "Ghost", null, null, null, null)))
        .verifyComplete();
    StepVerifier.create(repository.getHorseById(9999L)).verifyComplete();
  }

  @Test
  void deleteMissingHorseReturnsFalse() {
    StepVerifier.create(repository.deleteHorse(9999L)).expectNext(false).verifyComplete();
  }

  @Test
  void addAndDeleteFeedingSchedule() {
    Horse horse = create("Pepper");
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

  @Test
  void feedingChangesOnMissingHorse() {
    StepVerifier.create(repository.addFeedingSchedule(9999L, schedule(9, "Hay", null))).verifyComplete();
    StepVerifier.create(repository.deleteFeedingSchedule(9999L, 1L)).expectNext(false).verifyComplete();
  }

  @Test
  void concurrentFeedingAdditionsAreAllKept() {
    Horse horse = create("Busy");

    // each addition rewrites the horse item; the version check plus retry must not lose any
    Flux.range(6, 5)
        .flatMap(hour -> repository.addFeedingSchedule(horse.id(), schedule(hour, "Hay", null)))
        .blockLast();

    StepVerifier.create(repository.getFeedingSchedules(horse.id()).map(FeedingSchedule::feedingTime))
        .expectNext(LocalTime.of(6, 0), LocalTime.of(7, 0), LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0))
        .verifyComplete();
  }
}
