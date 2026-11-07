package com.coding2themax.petstore.data.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;
import com.coding2themax.petstore.data.repo.HorseRepository;
import com.coding2themax.petstore.data.service.HorseServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration test using the WebFlux test slice: the real controller, service, JSON mapping,
 * validation and exception handler run together; only the database repository is mocked.
 */
@WebFluxTest(controllers = HorseController.class)
@Import(HorseServiceImpl.class)
class HorseWebIntegrationTest {

  @MockitoBean
  private HorseRepository repository;

  @Autowired
  private WebTestClient webTestClient;

  private final FeedingSchedule hay = new FeedingSchedule(10L, 1L, LocalTime.of(7, 0), "Hay",
      new BigDecimal("5.00"), "kg", null);
  private final Horse thunder = new Horse(1L, "Thunder", "Thoroughbred", 7, "Alex", List.of(hay));

  @Test
  void createHorseParsesJsonAndPassesItThroughTheService() {
    BDDMockito.given(repository.createHorse(any())).willAnswer(inv -> Mono.just(((Horse) inv.getArgument(0)).withId(1L)));
    Map<String, Object> body = Map.of("name", "Thunder", "age", 7,
        "feedingSchedules", List.of(Map.of("feedingTime", "07:30", "feedType", "Hay", "quantity", 5, "unit", "kg")));

    webTestClient.post().uri("/horse").contentType(MediaType.APPLICATION_JSON).bodyValue(body)
        .exchange()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("$.id").isEqualTo(1)
        .jsonPath("$.feedingSchedules[0].feedingTime").isEqualTo("07:30:00");

    ArgumentCaptor<Horse> saved = ArgumentCaptor.forClass(Horse.class);
    verify(repository).createHorse(saved.capture());
    BDDMockito.then(repository).shouldHaveNoMoreInteractions();
    org.junit.jupiter.api.Assertions.assertEquals(LocalTime.of(7, 30),
        saved.getValue().feedingSchedules().get(0).feedingTime());
  }

  @Test
  void missingHorseReturns404WithMessage() {
    BDDMockito.given(repository.getHorseById(9L)).willReturn(Mono.empty());

    webTestClient.get().uri("/horse/9").exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class).isEqualTo("No horse found with id 9");
  }

  @Test
  void updateMissingHorseReturns404() {
    BDDMockito.given(repository.updateHorse(eq(9L), any())).willReturn(Mono.empty());

    webTestClient.put().uri("/horse/9").contentType(MediaType.APPLICATION_JSON).bodyValue(thunder)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void deleteHorseReturns204Or404() {
    BDDMockito.given(repository.deleteHorse(1L)).willReturn(Mono.just(true));
    BDDMockito.given(repository.deleteHorse(9L)).willReturn(Mono.just(false));

    webTestClient.delete().uri("/horse/1").exchange().expectStatus().isNoContent();
    webTestClient.delete().uri("/horse/9").exchange().expectStatus().isNotFound();
  }

  @Test
  void feedingSchedulesOfMissingHorseReturn404() {
    BDDMockito.given(repository.getHorseById(9L)).willReturn(Mono.empty());
    BDDMockito.given(repository.getFeedingSchedules(9L)).willReturn(Flux.empty());

    webTestClient.get().uri("/horse/9/feeding-schedule").exchange().expectStatus().isNotFound();
  }

  @Test
  void feedingSchedulesOfKnownHorseAreReturned() {
    BDDMockito.given(repository.getHorseById(1L)).willReturn(Mono.just(thunder));
    BDDMockito.given(repository.getFeedingSchedules(1L)).willReturn(Flux.just(hay));

    webTestClient.get().uri("/horse/1/feeding-schedule").exchange()
        .expectStatus().isOk()
        .expectBodyList(FeedingSchedule.class).hasSize(1).contains(hay);
  }

  @Test
  void addingFeedingToMissingHorseReturns404AndSavesNothing() {
    BDDMockito.given(repository.getHorseById(9L)).willReturn(Mono.empty());

    webTestClient.post().uri("/horse/9/feeding-schedule").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(hay.withIds(null, null))
        .exchange()
        .expectStatus().isNotFound();

    verify(repository, never()).addFeedingSchedule(anyLong(), any());
  }

  @Test
  void deleteUnknownFeedingReturns404WithMessage() {
    BDDMockito.given(repository.deleteFeedingSchedule(1L, 99L)).willReturn(Mono.just(false));

    webTestClient.delete().uri("/horse/1/feeding-schedule/99").exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class).isEqualTo("No feeding schedule 99 found for horse 1");
  }

  @Test
  void invalidFeedingIsRejectedBeforeReachingTheRepository() {
    Map<String, Object> body = Map.of("feedType", "", "quantity", 0, "unit", "kg");

    webTestClient.post().uri("/horse/1/feeding-schedule").contentType(MediaType.APPLICATION_JSON).bodyValue(body)
        .exchange()
        .expectStatus().isBadRequest();

    BDDMockito.then(repository).shouldHaveNoInteractions();
  }

  @Test
  void negativeAgeIsRejected() {
    webTestClient.post().uri("/horse").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of("name", "Thunder", "age", -1))
        .exchange()
        .expectStatus().isBadRequest();

    BDDMockito.then(repository).shouldHaveNoInteractions();
  }

  @Test
  void malformedTimeIsRejected() {
    Map<String, Object> body = Map.of("feedingTime", "noon", "feedType", "Hay", "quantity", 1, "unit", "kg");

    webTestClient.post().uri("/horse/1/feeding-schedule").contentType(MediaType.APPLICATION_JSON).bodyValue(body)
        .exchange()
        .expectStatus().isBadRequest();
  }
}
