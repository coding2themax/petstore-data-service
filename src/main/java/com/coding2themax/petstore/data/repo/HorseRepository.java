package com.coding2themax.petstore.data.repo;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HorseRepository {

  Flux<Horse> findAllHorses();

  Mono<Horse> getHorseById(Long id);

  /** Saves a new horse and its feeding schedules. */
  Mono<Horse> createHorse(Horse horse);

  /** Updates the horse and replaces its feeding schedules. Empty if the horse does not exist. */
  Mono<Horse> updateHorse(Long id, Horse horse);

  /** Deletes the horse and its feeding schedules. True if a horse was deleted. */
  Mono<Boolean> deleteHorse(Long id);

  Flux<FeedingSchedule> getFeedingSchedules(Long horseId);

  Mono<FeedingSchedule> addFeedingSchedule(Long horseId, FeedingSchedule schedule);

  /** True if the feeding schedule existed for that horse and was deleted. */
  Mono<Boolean> deleteFeedingSchedule(Long horseId, Long scheduleId);
}
