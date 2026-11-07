package com.coding2themax.petstore.data.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openapitools.client.model.Pet;
import org.springframework.beans.factory.annotation.Autowired;

import com.coding2themax.petstore.data.support.DynamoDbTest;

import reactor.test.StepVerifier;

/** Integration test of the pet repository against DynamoDB Local, using the sample data. */
@DynamoDbTest
class PetDynamoRepositoryTest {

  @Autowired
  private PetDynamoRepository repository;

  @Test
  void findAllPets() {
    StepVerifier.create(repository.findAllPets().map(Pet::getName))
        .expectNext("Angelfish", "Tiger Shark")
        .verifyComplete();
  }

  @Test
  void getPetByIdReturnsTheWholePet() {
    StepVerifier.create(repository.getPetById(1L))
        .assertNext(pet -> {
          assertEquals("Angelfish", pet.getName());
          assertEquals("Fish", pet.getCategory().getName());
          assertEquals(2, pet.getPhotoUrls().size());
          assertEquals(List.of("tag1", "tag2"), pet.getTags().stream().map(t -> t.getName()).toList());
          assertEquals(Pet.StatusEnum.AVAILABLE, pet.getStatus());
        })
        .verifyComplete();
  }

  @Test
  void getMissingPetIsEmpty() {
    StepVerifier.create(repository.getPetById(999L)).verifyComplete();
  }

  @Test
  void getPetsByStatusUsesTheStatusIndex() {
    StepVerifier.create(repository.getPetsByStatus(List.of("available"))).expectNextCount(2).verifyComplete();
  }

  @Test
  void getPetsBySeveralStatusesMergesResults() {
    StepVerifier.create(repository.getPetsByStatus(List.of("available", "pending", "available")))
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void getPetsByUnusedStatusIsEmpty() {
    StepVerifier.create(repository.getPetsByStatus(List.of("sold"))).verifyComplete();
  }

  @Test
  void getPetsByTagsMatchesAnyTag() {
    StepVerifier.create(repository.getPetsByTags(List.of("tag1", "tag2")).map(Pet::getId))
        .expectNext(1L)
        .verifyComplete();
    StepVerifier.create(repository.getPetsByTags(List.of("tag1", "tag3")).map(Pet::getId))
        .expectNext(1L, 2L)
        .verifyComplete();
  }

  @Test
  void getPetsByUnknownOrNoTagsIsEmpty() {
    StepVerifier.create(repository.getPetsByTags(List.of("nope"))).verifyComplete();
    StepVerifier.create(repository.getPetsByTags(List.of())).verifyComplete();
  }
}
