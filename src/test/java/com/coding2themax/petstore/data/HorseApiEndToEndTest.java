package com.coding2themax.petstore.data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.coding2themax.petstore.data.model.Horse;
import com.coding2themax.petstore.data.support.DynamoDbLocalContainer;

/**
 * Starts the whole application on a random port against DynamoDB Local and drives the horse API
 * over HTTP.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
    "petstore.dynamodb.table-prefix=e2e-",
    "petstore.dynamodb.access-key-id=test",
    "petstore.dynamodb.secret-access-key=test",
    "petstore.dynamodb.create-tables=true",
    "petstore.dynamodb.seed-data=true" })
@AutoConfigureWebTestClient
class HorseApiEndToEndTest {

  @TestConfiguration(proxyBeanMethods = false)
  static class DynamoDbLocal {
    @Bean
    static DynamicPropertyRegistrar dynamoDbLocalEndpoint() {
      return registry -> registry.add("petstore.dynamodb.endpoint", DynamoDbLocalContainer::endpoint);
    }
  }

  @Autowired
  private WebTestClient client;

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
  }

  @Test
  void samplePetsAreServed() {
    client.get().uri("/pet/findByStatus?status=available").exchange()
        .expectStatus().isOk()
        .expectBody().jsonPath("$.length()").isEqualTo(2);
  }

  @Test
  void sampleHorseFeedingsAreServed() {
    client.get().uri("/horse/1/feeding-schedule").exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$[0].feedingTime").isEqualTo(LocalTime.of(7, 0).toString() + ":00")
        .jsonPath("$[1].quantity").isEqualTo(new BigDecimal("2.5").doubleValue());
  }
}
