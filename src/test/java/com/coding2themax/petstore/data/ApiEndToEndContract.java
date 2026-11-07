package com.coding2themax.petstore.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.coding2themax.petstore.data.model.Horse;
import com.coding2themax.petstore.data.repo.HorseRepository;
import com.coding2themax.petstore.data.repo.PetRepository;

/**
 * Starts the whole application against one storage backend and drives the API over HTTP. Each
 * subclass picks the backend.
 */
abstract class ApiEndToEndContract {

  @Autowired
  protected WebTestClient client;

  @Autowired
  protected ApplicationContext context;

  /** The repository classes the selected storage should provide. */
  protected abstract List<Class<?>> expectedRepositories();

  /** A bean type that must not exist when this storage is selected. */
  protected abstract Class<?> otherStorageBean();

  @Test
  void onlyTheSelectedStorageIsWiredUp() {
    assertEquals(expectedRepositories(), List.of(
        AopUtils.getTargetClass(context.getBean(PetRepository.class)),
        AopUtils.getTargetClass(context.getBean(HorseRepository.class))));
    assertTrue(context.getBeansOfType(otherStorageBean()).isEmpty());
  }

  @Test
  void horseLifecycleOverHttp() {
    Map<String, Object> body = Map.of("name", "Comet", "breed", "Arabian",
        "feedingSchedules", List.of(Map.of("feedingTime", "06:30", "feedType", "Hay", "quantity", 4.5, "unit", "kg")));

    Horse created = client.post().uri("/horse").contentType(MediaType.APPLICATION_JSON).bodyValue(body)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Horse.class).returnResult().getResponseBody();

    client.post().uri("/horse/{id}/feeding-schedule", created.id()).contentType(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of("feedingTime", "05:00", "feedType", "Oats", "quantity", 1, "unit", "kg"))
        .exchange()
        .expectStatus().isCreated();

    client.get().uri("/horse/{id}", created.id()).exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.name").isEqualTo("Comet")
        .jsonPath("$.feedingSchedules.length()").isEqualTo(2)
        .jsonPath("$.feedingSchedules[0].feedType").isEqualTo("Oats")
        .jsonPath("$.feedingSchedules[1].feedingTime").isEqualTo("06:30:00");

    client.delete().uri("/horse/{id}", created.id()).exchange().expectStatus().isNoContent();
    client.get().uri("/horse/{id}", created.id()).exchange().expectStatus().isNotFound();
    client.post().uri("/horse/{id}/feeding-schedule", created.id()).contentType(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of("feedingTime", "05:00", "feedType", "Oats", "quantity", 1, "unit", "kg"))
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void samplePetsAreServed() {
    client.get().uri("/pet/findByStatus?status=available").exchange()
        .expectStatus().isOk()
        .expectBody().jsonPath("$.length()").isEqualTo(2);
    client.get().uri("/pet/findByTags?tags=tag3").exchange()
        .expectStatus().isOk()
        .expectBody().jsonPath("$[0].name").isEqualTo("Tiger Shark");
  }

  @Test
  void sampleHorseFeedingsAreServed() {
    client.get().uri("/horse/1/feeding-schedule").exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.length()").isEqualTo(2)
        .jsonPath("$[0].feedingTime").isEqualTo("07:00:00")
        .jsonPath("$[1].feedType").isEqualTo("Oats");
  }
}
