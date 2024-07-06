package com.coding2themax.petstore.data.repo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import reactor.test.StepVerifier;

@SpringBootTest
@Testcontainers
public class PetR2DBCRepoTest {

  @Autowired
  private PetR2DBCRepo service;

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
    this.service.getPetsByStatus(Collections.singletonList("available")).as(StepVerifier::create)
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void testGetPetsByStatusEmpty() {

    List<String> status = Arrays.asList("available", "pending");

    this.service.getPetsByStatus(status).as(StepVerifier::create)
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void testGetPetsByTags() {

    List<String> tags = Arrays.asList("tag1", "tag2");

    this.service.getPetsByTags(tags).as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete();
  }
}
