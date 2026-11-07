package com.coding2themax.petstore.data.controller;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.coding2themax.petstore.data.exception.HorseNotFoundException;
import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;
import com.coding2themax.petstore.data.service.HorseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = HorseController.class)
class HorseControllerTest {

  @MockitoBean
  private HorseService service;

  @Autowired
  private WebTestClient webTestClient;

  private final FeedingSchedule hay = new FeedingSchedule(1L, 1L, LocalTime.of(7, 0), "Hay",
      new BigDecimal("5.00"), "kg", "Morning feed");

  private final Horse thunder = new Horse(1L, "Thunder", "Thoroughbred", 7, "Alex", List.of(hay));

  @Test
  void getAllHorses() {
    BDDMockito.given(service.findAllHorses()).willReturn(Flux.just(thunder));

    webTestClient.get().uri("/horse/all").exchange()
        .expectStatus().isOk()
        .expectBodyList(Horse.class).hasSize(1).contains(thunder);
  }

  @Test
  void getHorseById() {
    BDDMockito.given(service.getHorseById(1L)).willReturn(Mono.just(thunder));

    webTestClient.get().uri("/horse/1").exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.name").isEqualTo("Thunder")
        .jsonPath("$.feedingSchedules[0].feedingTime").isEqualTo("07:00:00")
        .jsonPath("$.feedingSchedules[0].feedType").isEqualTo("Hay");
  }

  @Test
  void getHorseByIdNotFound() {
    BDDMockito.given(service.getHorseById(9L)).willReturn(Mono.error(new HorseNotFoundException("missing")));

    webTestClient.get().uri("/horse/9").exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class).isEqualTo("missing");
  }

  @Test
  void createHorse() {
    BDDMockito.given(service.createHorse(ArgumentMatchers.any(Horse.class))).willReturn(Mono.just(thunder));

    webTestClient.post().uri("/horse").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(thunder.withId(null))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Horse.class).isEqualTo(thunder);
  }

  @Test
  void createHorseWithoutNameIsBadRequest() {
    webTestClient.post().uri("/horse").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of("breed", "Arabian"))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void createHorseWithInvalidScheduleIsBadRequest() {
    Map<String, Object> body = Map.of("name", "Thunder",
        "feedingSchedules", List.of(Map.of("feedType", "Hay", "quantity", -1, "unit", "kg")));

    webTestClient.post().uri("/horse").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void updateHorse() {
    BDDMockito.given(service.updateHorse(ArgumentMatchers.eq(1L), ArgumentMatchers.any(Horse.class)))
        .willReturn(Mono.just(thunder));

    webTestClient.put().uri("/horse/1").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(thunder)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Horse.class).isEqualTo(thunder);
  }

  @Test
  void deleteHorse() {
    BDDMockito.given(service.deleteHorse(1L)).willReturn(Mono.empty());

    webTestClient.delete().uri("/horse/1").exchange().expectStatus().isNoContent();
  }

  @Test
  void getFeedingSchedules() {
    BDDMockito.given(service.getFeedingSchedules(1L)).willReturn(Flux.just(hay));

    webTestClient.get().uri("/horse/1/feeding-schedule").exchange()
        .expectStatus().isOk()
        .expectBodyList(FeedingSchedule.class).hasSize(1).contains(hay);
  }

  @Test
  void addFeedingSchedule() {
    BDDMockito.given(service.addFeedingSchedule(ArgumentMatchers.eq(1L), ArgumentMatchers.any(FeedingSchedule.class)))
        .willReturn(Mono.just(hay));

    webTestClient.post().uri("/horse/1/feeding-schedule").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(hay.withIds(null, null))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(FeedingSchedule.class).isEqualTo(hay);
  }

  @Test
  void deleteFeedingSchedule() {
    BDDMockito.given(service.deleteFeedingSchedule(1L, 2L)).willReturn(Mono.empty());

    webTestClient.delete().uri("/horse/1/feeding-schedule/2").exchange().expectStatus().isNoContent();
  }
}
