package com.coding2themax.petstore.data.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

import reactor.test.StepVerifier;

/**
 * Runs the horse repository contract against Postgres using the R2DBC test slice, plus checks that
 * multi-statement writes roll back as a whole.
 */
@DataR2dbcTest
@Import(HorseR2DBCService.class)
class HorseR2DBCServiceTest extends HorseRepositoryContract {

  @Autowired
  private HorseR2DBCService repository;

  @Override
  protected HorseRepository repository() {
    return repository;
  }

  // a non-positive quantity skips bean validation here but breaks the database check constraint
  private static final FeedingSchedule INVALID = new FeedingSchedule(null, null, LocalTime.NOON, "Hay",
      BigDecimal.ZERO, "kg", null);

  @Test
  void createHorseRollsBackWhenAScheduleFailsToSave() {
    Horse horse = new Horse(null, "Rollback", null, null, null, List.of(schedule(8, "Hay", null), INVALID));

    StepVerifier.create(repository.createHorse(horse)).expectError().verify();

    StepVerifier.create(repository.findAllHorses().filter(h -> "Rollback".equals(h.name()))).verifyComplete();
  }

  @Test
  void updateHorseRollsBackWhenAScheduleFailsToSave() {
    Horse original = create("Steady", schedule(7, "Hay", null));

    StepVerifier.create(repository.updateHorse(original.id(),
        new Horse(null, "Renamed", null, null, null, List.of(INVALID)))).expectError().verify();

    Horse after = repository.getHorseById(original.id()).block();
    assertNotNull(after);
    assertEquals("Steady", after.name());
    assertEquals(List.of("Hay"), after.feedingSchedules().stream().map(FeedingSchedule::feedType).toList());
  }
}
