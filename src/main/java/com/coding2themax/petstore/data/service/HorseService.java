package com.coding2themax.petstore.data.service;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HorseService {

  Flux<Horse> findAllHorses();

  Mono<Horse> getHorseById(Long id);

  Mono<Horse> createHorse(Horse horse);

  Mono<Horse> updateHorse(Long id, Horse horse);

  Mono<Void> deleteHorse(Long id);

  Flux<FeedingSchedule> getFeedingSchedules(Long horseId);

  Mono<FeedingSchedule> addFeedingSchedule(Long horseId, FeedingSchedule schedule);

  Mono<Void> deleteFeedingSchedule(Long horseId, Long scheduleId);
}
