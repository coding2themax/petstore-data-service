package com.coding2themax.petstore.data.controller;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.openapitools.client.model.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.coding2themax.petstore.data.service.PetService;

import reactor.core.publisher.Flux;

@WebFluxTest(controllers = PetController.class)
public class PetControllerTest {

  @MockBean
  private PetService service;

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testGetAllPets() {

    Pet pet1 = new Pet();
    pet1.setId(1L);
    pet1.setName("pet1");
    pet1.setStatus(Pet.StatusEnum.AVAILABLE);

    Pet pet2 = new Pet();
    pet2.setId(2L);
    pet2.setName("pet2");
    pet2.setStatus(Pet.StatusEnum.PENDING);

    BDDMockito.given(service.findAllPets()).willReturn(Flux.just(pet1, pet2));

    this.webTestClient.get().uri("/pets/all").exchange().expectStatus().isOk().expectBodyList(Pet.class)
        .hasSize(2).contains(pet1, pet2);
  }
}
