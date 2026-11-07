package com.coding2themax.petstore.data.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;
import com.coding2themax.petstore.data.service.HorseService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/horse")
public class HorseController {

  private final HorseService service;

  public HorseController(HorseService service) {
    this.service = service;
  }

  @GetMapping("/all")
  public Flux<Horse> getAllHorses() {
    return service.findAllHorses();
  }

  @GetMapping("/{horseId}")
  public Mono<Horse> getHorseById(@PathVariable Long horseId) {
    return service.getHorseById(horseId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Horse> createHorse(@Valid @RequestBody Horse horse) {
    return service.createHorse(horse);
  }

  @PutMapping("/{horseId}")
  public Mono<Horse> updateHorse(@PathVariable Long horseId, @Valid @RequestBody Horse horse) {
    return service.updateHorse(horseId, horse);
  }

  @DeleteMapping("/{horseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteHorse(@PathVariable Long horseId) {
    return service.deleteHorse(horseId);
  }

  @GetMapping("/{horseId}/feeding-schedule")
  public Flux<FeedingSchedule> getFeedingSchedules(@PathVariable Long horseId) {
    return service.getFeedingSchedules(horseId);
  }

  @PostMapping("/{horseId}/feeding-schedule")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<FeedingSchedule> addFeedingSchedule(@PathVariable Long horseId,
      @Valid @RequestBody FeedingSchedule schedule) {
    return service.addFeedingSchedule(horseId, schedule);
  }

  @DeleteMapping("/{horseId}/feeding-schedule/{scheduleId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteFeedingSchedule(@PathVariable Long horseId, @PathVariable Long scheduleId) {
    return service.deleteFeedingSchedule(horseId, scheduleId);
  }
}
