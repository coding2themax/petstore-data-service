package com.coding2themax.petstore.data.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Tag;

import reactor.test.StepVerifier;

/**
 * Behaviour every {@link PetRepository} must have, run against each storage backend with the
 * same sample data (pets 1 Angelfish and 2 Tiger Shark).
 */
abstract class PetRepositoryContract {

  protected abstract PetRepository repository();

  @Test
  void findAllPetsInIdOrder() {
    StepVerifier.create(repository().findAllPets().map(Pet::getName))
        .expectNext("Angelfish", "Tiger Shark")
        .verifyComplete();
  }

  @Test
  void getPetByIdReturnsTheWholePet() {
    StepVerifier.create(repository().getPetById(1L))
        .assertNext(pet -> {
          assertEquals("Angelfish", pet.getName());
          assertEquals(1L, pet.getCategory().getId());
          assertEquals("Fish", pet.getCategory().getName());
          assertEquals(List.of("http://www.jpetstore.com/images/fish1.gif", "http://www.jpetstore.com/images/fish2.gif"),
              pet.getPhotoUrls());
          assertEquals(List.of("tag1", "tag2"), pet.getTags().stream().map(Tag::getName).toList());
          assertEquals(Pet.StatusEnum.AVAILABLE, pet.getStatus());
        })
        .verifyComplete();
  }

  @Test
  void getMissingPetIsEmpty() {
    StepVerifier.create(repository().getPetById(999L)).verifyComplete();
  }

  @Test
  void getPetsByStatus() {
    StepVerifier.create(repository().getPetsByStatus(List.of("available")).map(Pet::getId))
        .expectNext(1L, 2L)
        .verifyComplete();
  }

  @Test
  void getPetsBySeveralStatusesReturnsEachPetOnce() {
    StepVerifier.create(repository().getPetsByStatus(List.of("available", "pending", "available")))
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void getPetsByUnusedOrNoStatusIsEmpty() {
    StepVerifier.create(repository().getPetsByStatus(List.of("sold"))).verifyComplete();
    StepVerifier.create(repository().getPetsByStatus(List.of())).verifyComplete();
  }

  @Test
  void getPetsByTagsMatchesAnyTagAndReturnsEachPetOnce() {
    StepVerifier.create(repository().getPetsByTags(List.of("tag1", "tag2")).map(Pet::getId))
        .expectNext(1L)
        .verifyComplete();
    StepVerifier.create(repository().getPetsByTags(List.of("tag1", "tag3")).map(Pet::getId))
        .expectNext(1L, 2L)
        .verifyComplete();
  }

  @Test
  void getPetsByTagsReturnsAllTagsOfMatchingPets() {
    StepVerifier.create(repository().getPetsByTags(List.of("tag2")))
        .assertNext(pet -> assertEquals(Set.of("tag1", "tag2"),
            pet.getTags().stream().map(Tag::getName).collect(Collectors.toSet())))
        .verifyComplete();
  }

  @Test
  void getPetsByUnknownOrNoTagsIsEmpty() {
    StepVerifier.create(repository().getPetsByTags(List.of("nope"))).verifyComplete();
    StepVerifier.create(repository().getPetsByTags(List.of())).verifyComplete();
  }

  @Test
  void tagValuesAreDataNotQueryText() {
    StepVerifier.create(repository().getPetsByTags(List.of("x') or ('1'='1", "o'brien"))).verifyComplete();
  }
}
