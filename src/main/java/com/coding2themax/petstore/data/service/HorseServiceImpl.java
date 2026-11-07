package com.coding2themax.petstore.data.service;

import org.springframework.stereotype.Service;

import com.coding2themax.petstore.data.exception.HorseNotFoundException;
import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;
import com.coding2themax.petstore.data.repo.HorseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HorseServiceImpl implements HorseService {

  private final HorseRepository repository;

  public HorseServiceImpl(HorseRepository repository) {
    this.repository = repository;
  }

  @Override
  public Flux<Horse> findAllHorses() {
    return repository.findAllHorses();
  }

  @Override
  public Mono<Horse> getHorseById(Long id) {
    return repository.getHorseById(id).switchIfEmpty(Mono.error(() -> horseNotFound(id)));
  }

  @Override
  public Mono<Horse> createHorse(Horse horse) {
    return repository.createHorse(horse);
  }

  @Override
  public Mono<Horse> updateHorse(Long id, Horse horse) {
    return repository.updateHorse(id, horse).switchIfEmpty(Mono.error(() -> horseNotFound(id)));
  }

  @Override
  public Mono<Void> deleteHorse(Long id) {
    return repository.deleteHorse(id)
        .flatMap(deleted -> deleted ? Mono.<Void>empty() : Mono.error(horseNotFound(id)));
  }

  @Override
  public Flux<FeedingSchedule> getFeedingSchedules(Long horseId) {
    return getHorseById(horseId).thenMany(Flux.defer(() -> repository.getFeedingSchedules(horseId)));
  }

  @Override
  public Mono<FeedingSchedule> addFeedingSchedule(Long horseId, FeedingSchedule schedule) {
    return getHorseById(horseId).then(Mono.defer(() -> repository.addFeedingSchedule(horseId, schedule)));
  }

  @Override
  public Mono<Void> deleteFeedingSchedule(Long horseId, Long scheduleId) {
    return repository.deleteFeedingSchedule(horseId, scheduleId)
        .flatMap(deleted -> deleted ? Mono.<Void>empty()
            : Mono.error(new HorseNotFoundException(
                "No feeding schedule " + scheduleId + " found for horse " + horseId)));
  }

  private static HorseNotFoundException horseNotFound(Long id) {
    return new HorseNotFoundException("No horse found with id " + id);
  }
}
