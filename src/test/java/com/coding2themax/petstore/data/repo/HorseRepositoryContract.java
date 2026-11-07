package com.coding2themax.petstore.data.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Behaviour every {@link HorseRepository} must have, run against each storage backend with the
 * same sample data (horse 1 Thunder with feedings 1 and 2, horse 2 Daisy with feeding 3).
 */
abstract class HorseRepositoryContract {

  protected abstract HorseRepository repository();

  protected static FeedingSchedule schedule(int hour, String feedType, String notes) {
    return new FeedingSchedule(null, null, LocalTime.of(hour, 0), feedType, new BigDecimal("3.5"), "kg", notes);
  }

  protected Horse create(String name, FeedingSchedule... schedules) {
    Horse horse = repository().createHorse(new Horse(null, name, null, null, null, List.of(schedules))).block();
    assertNotNull(horse);
    return horse;
  }

  @Test
  void findAllHorsesIncludesSampleHorsesInIdOrder() {
    StepVerifier.create(repository().findAllHorses().collectList())
        .assertNext(horses -> {
          assertEquals(List.of(1L, 2L), horses.stream().map(Horse::id).limit(2).toList());
          assertEquals("Thunder", horses.get(0).name());
          assertEquals(2, horses.get(0).feedingSchedules().size());
          assertEquals("Daisy", horses.get(1).name());
        })
        .verifyComplete();
  }

  @Test
  void getHorseById() {
    StepVerifier.create(repository().getHorseById(2L))
        .assertNext(horse -> {
          assertEquals("Daisy", horse.name());
          assertEquals("Quarter Horse", horse.breed());
          assertEquals(12, horse.age());
          assertEquals("Sam", horse.owner());
          FeedingSchedule hay = horse.feedingSchedules().get(0);
          assertEquals(3L, hay.id());
          assertEquals(2L, hay.horseId());
          assertEquals(LocalTime.of(8, 0), hay.feedingTime());
          // Postgres keeps the column scale (4.00), DynamoDB does not (4): compare by value
          assertEquals(0, new BigDecimal("4").compareTo(hay.quantity()));
          assertNull(hay.notes());
        })
        .verifyComplete();
  }

  @Test
  void getHorseByIdMissing() {
    StepVerifier.create(repository().getHorseById(9999L)).verifyComplete();
  }

  @Test
  void createReadUpdateAndDeleteHorse() {
    Horse created = create("Comet", schedule(19, "Grain", null), schedule(6, "Hay", "Soak first"));
    assertTrue(created.id() > 2, "new ids continue after the sample data");
    assertTrue(created.feedingSchedules().stream().allMatch(s -> s.id() != null && created.id().equals(s.horseId())));

    StepVerifier.create(repository().getHorseById(created.id()))
        .assertNext(found -> {
          assertEquals("Comet", found.name());
          assertNull(found.breed());
          assertEquals(List.of("Hay", "Grain"), found.feedingSchedules().stream().map(FeedingSchedule::feedType).toList());
          assertEquals("Soak first", found.feedingSchedules().get(0).notes());
        })
        .verifyComplete();

    Horse changed = new Horse(null, "Comet", "Arabian", 4, "Jo", List.of(schedule(12, "Carrots", null)));
    StepVerifier.create(repository().updateHorse(created.id(), changed))
        .assertNext(updated -> {
          assertEquals(created.id(), updated.id());
          assertEquals("Arabian", updated.breed());
          assertEquals(1, updated.feedingSchedules().size());
        })
        .verifyComplete();
    StepVerifier.create(repository().getFeedingSchedules(created.id()).map(FeedingSchedule::feedType))
        .expectNext("Carrots")
        .verifyComplete();

    StepVerifier.create(repository().deleteHorse(created.id())).expectNext(true).verifyComplete();
    StepVerifier.create(repository().getHorseById(created.id())).verifyComplete();
    StepVerifier.create(repository().getFeedingSchedules(created.id())).verifyComplete();
  }

  @Test
  void horseWithoutFeedingsHasEmptySchedule() {
    Horse bare = create("Bare");

    StepVerifier.create(repository().getHorseById(bare.id()))
        .assertNext(horse -> assertTrue(horse.feedingSchedules().isEmpty()))
        .verifyComplete();
  }

  @Test
  void updateMissingHorseIsEmptyAndDoesNotCreateIt() {
    StepVerifier.create(repository().updateHorse(9999L, new Horse(null, "Ghost", null, null, null, null)))
        .verifyComplete();
    StepVerifier.create(repository().getHorseById(9999L)).verifyComplete();
  }

  @Test
  void deleteMissingHorseReturnsFalse() {
    StepVerifier.create(repository().deleteHorse(9999L)).expectNext(false).verifyComplete();
  }

  @Test
  void addAndDeleteFeedingSchedule() {
    Horse horse = create("Pepper");

    FeedingSchedule added = repository().addFeedingSchedule(horse.id(), schedule(9, "Hay", null)).block();
    assertNotNull(added);
    assertNotNull(added.id());
    assertEquals(horse.id(), added.horseId());

    StepVerifier.create(repository().getFeedingSchedules(horse.id()).map(FeedingSchedule::id))
        .expectNext(added.id())
        .verifyComplete();

    StepVerifier.create(repository().deleteFeedingSchedule(horse.id(), added.id())).expectNext(true).verifyComplete();
    StepVerifier.create(repository().deleteFeedingSchedule(horse.id(), added.id())).expectNext(false).verifyComplete();
    StepVerifier.create(repository().getFeedingSchedules(horse.id())).verifyComplete();
  }

  @Test
  void feedingBelongsToItsHorse() {
    // feeding 3 belongs to Daisy (2), not Thunder (1)
    StepVerifier.create(repository().deleteFeedingSchedule(1L, 3L)).expectNext(false).verifyComplete();
  }

  @Test
  void feedingChangesOnMissingHorseDoNothing() {
    StepVerifier.create(repository().addFeedingSchedule(9999L, schedule(9, "Hay", null))).verifyComplete();
    StepVerifier.create(repository().deleteFeedingSchedule(9999L, 1L)).expectNext(false).verifyComplete();
  }

  @Test
  void concurrentFeedingAdditionsAreAllKept() {
    Horse horse = create("Busy");

    Flux.range(6, 5)
        .flatMap(hour -> repository().addFeedingSchedule(horse.id(), schedule(hour, "Hay", null)))
        .blockLast();

    StepVerifier.create(repository().getFeedingSchedules(horse.id()).map(FeedingSchedule::feedingTime))
        .expectNext(LocalTime.of(6, 0), LocalTime.of(7, 0), LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0))
        .verifyComplete();
  }
}
