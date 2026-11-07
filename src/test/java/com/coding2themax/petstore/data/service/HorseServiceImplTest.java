package com.coding2themax.petstore.data.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import com.coding2themax.petstore.data.exception.HorseNotFoundException;
import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;
import com.coding2themax.petstore.data.repo.HorseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class HorseServiceImplTest {

  private HorseRepository repository;
  private HorseServiceImpl service;

  private final FeedingSchedule hay = new FeedingSchedule(1L, 1L, LocalTime.of(7, 0), "Hay",
      BigDecimal.TEN, "kg", null);
  private final Horse thunder = new Horse(1L, "Thunder", null, null, null, List.of(hay));

  @BeforeEach
  void setUp() {
    repository = mock(HorseRepository.class);
    service = new HorseServiceImpl(repository);
  }

  @Test
  void findAllHorses() {
    when(repository.findAllHorses()).thenReturn(Flux.just(thunder));
    StepVerifier.create(service.findAllHorses()).expectNext(thunder).verifyComplete();
  }

  @Test
  void getHorseById() {
    when(repository.getHorseById(1L)).thenReturn(Mono.just(thunder));
    StepVerifier.create(service.getHorseById(1L)).expectNext(thunder).verifyComplete();
  }

  @Test
  void getHorseByIdNotFound() {
    when(repository.getHorseById(1L)).thenReturn(Mono.empty());
    StepVerifier.create(service.getHorseById(1L)).verifyError(HorseNotFoundException.class);
  }

  @Test
  void createHorse() {
    when(repository.createHorse(thunder)).thenReturn(Mono.just(thunder));
    StepVerifier.create(service.createHorse(thunder)).expectNext(thunder).verifyComplete();
  }

  @Test
  void updateHorse() {
    when(repository.updateHorse(1L, thunder)).thenReturn(Mono.just(thunder));
    StepVerifier.create(service.updateHorse(1L, thunder)).expectNext(thunder).verifyComplete();
  }

  @Test
  void updateHorseNotFound() {
    when(repository.updateHorse(1L, thunder)).thenReturn(Mono.empty());
    StepVerifier.create(service.updateHorse(1L, thunder)).verifyError(HorseNotFoundException.class);
  }

  @Test
  void deleteHorse() {
    when(repository.deleteHorse(1L)).thenReturn(Mono.just(true));
    StepVerifier.create(service.deleteHorse(1L)).verifyComplete();
  }

  @Test
  void deleteHorseNotFound() {
    when(repository.deleteHorse(1L)).thenReturn(Mono.just(false));
    StepVerifier.create(service.deleteHorse(1L)).verifyError(HorseNotFoundException.class);
  }

  @Test
  void getFeedingSchedules() {
    when(repository.getHorseById(1L)).thenReturn(Mono.just(thunder));
    when(repository.getFeedingSchedules(1L)).thenReturn(Flux.just(hay));
    StepVerifier.create(service.getFeedingSchedules(1L)).expectNext(hay).verifyComplete();
  }

  @Test
  void getFeedingSchedulesHorseNotFound() {
    when(repository.getHorseById(1L)).thenReturn(Mono.empty());
    StepVerifier.create(service.getFeedingSchedules(1L)).verifyError(HorseNotFoundException.class);
    verify(repository, never()).getFeedingSchedules(1L);
  }

  @Test
  void addFeedingSchedule() {
    when(repository.getHorseById(1L)).thenReturn(Mono.just(thunder));
    when(repository.addFeedingSchedule(1L, hay)).thenReturn(Mono.just(hay));
    StepVerifier.create(service.addFeedingSchedule(1L, hay)).expectNext(hay).verifyComplete();
  }

  @Test
  void addFeedingScheduleHorseNotFound() {
    when(repository.getHorseById(1L)).thenReturn(Mono.empty());
    StepVerifier.create(service.addFeedingSchedule(1L, hay)).verifyError(HorseNotFoundException.class);
    verify(repository, never()).addFeedingSchedule(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
  }

  @Test
  void deleteFeedingSchedule() {
    when(repository.deleteFeedingSchedule(1L, 1L)).thenReturn(Mono.just(true));
    StepVerifier.create(service.deleteFeedingSchedule(1L, 1L)).verifyComplete();
  }

  @Test
  void deleteFeedingScheduleNotFound() {
    when(repository.deleteFeedingSchedule(1L, 1L)).thenReturn(Mono.just(false));
    StepVerifier.create(service.deleteFeedingSchedule(1L, 1L)).verifyError(HorseNotFoundException.class);
  }
}
