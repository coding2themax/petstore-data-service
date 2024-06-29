package com.coding2themax.petstore.data.repo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapitools.client.model.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import reactor.test.StepVerifier;

@SpringBootTest
@Testcontainers
public class PetR2DBCServiceTest {

  @Autowired
  private PetR2DBCService service;

  @Test
  void testFindAllPets() {
    this.service.findAllPets().as(StepVerifier::create)
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void testContructor() {
    PetMapper petMapper = new PetMapper();
    Assertions.assertNotNull(petMapper);
  }

  @Test
  void testGetPetById() {
    this.service.getPetById(1L).as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void testGetPetsByStatus() {
    this.service.getPetsByStatus("available").as(StepVerifier::create)
        .expectNextCount(2)
        .verifyComplete();
  }
}
