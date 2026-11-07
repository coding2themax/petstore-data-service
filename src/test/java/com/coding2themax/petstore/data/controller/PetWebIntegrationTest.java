package com.coding2themax.petstore.data.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.openapitools.client.model.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.coding2themax.petstore.data.repo.PetRepository;
import com.coding2themax.petstore.data.service.PetStoreImpl;

import reactor.core.publisher.Flux;

/**
 * Integration test using the WebFlux test slice: the real pet controller, service and exception
 * handler run together; only the database repository is mocked.
 */
@WebFluxTest(controllers = PetController.class)
@Import(PetStoreImpl.class)
class PetWebIntegrationTest {

  @MockitoBean
  private PetRepository repository;

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void petsByTagsAreReturned() {
    Pet pet = new Pet().id(1L).name("Angelfish").status(Pet.StatusEnum.AVAILABLE);
    BDDMockito.given(repository.getPetsByTags(List.of("tag1"))).willReturn(Flux.just(pet));

    webTestClient.get().uri("/pet/findByTags?tags=tag1").exchange()
        .expectStatus().isOk()
        .expectBodyList(Pet.class).hasSize(1).contains(pet);
  }

  @Test
  void unknownTagReturns404() {
    BDDMockito.given(repository.getPetsByTags(List.of("nope"))).willReturn(Flux.empty());

    webTestClient.get().uri("/pet/findByTags?tags=nope").exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class).isEqualTo("Invalid tag value");
  }

  @Test
  void unknownStatusMatchReturns404() {
    BDDMockito.given(repository.getPetsByStatus(List.of("sold"))).willReturn(Flux.empty());

    webTestClient.get().uri("/pet/findByStatus?status=sold").exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class).isEqualTo("Invalid status value");
  }

  @Test
  void invalidStatusReturns400() {
    webTestClient.get().uri("/pet/findByStatus?status=lost").exchange().expectStatus().isBadRequest();

    BDDMockito.then(repository).shouldHaveNoInteractions();
  }
}
