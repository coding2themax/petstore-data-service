package com.coding2themax.petstore.data.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Tag;

import com.coding2themax.petstore.data.repo.PetRepository;
import com.coding2themax.petstore.data.repo.TagRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class PetStoreImplTest {

  @Mock
  private PetRepository repository;

  private TagRepository tagRepository;

  private PetStoreImpl petStore;

  public PetStoreImplTest() {
    MockitoAnnotations.openMocks(this);
    petStore = new PetStoreImpl(repository, tagRepository);
  }

  @Test
  void testFindAllPets() {
    // Mock the repository response
    Pet pet1 = new Pet();
    pet1.setId(1L);
    pet1.setName("Max");
    Pet pet2 = new Pet();
    pet2.setId(2L);
    pet2.setName("Bella");
    when(repository.findAllPets()).thenReturn(Flux.just(pet1, pet2));

    // Verify the result
    StepVerifier.create(petStore.findAllPets())
        .expectNext(pet1)
        .expectNext(pet2)
        .verifyComplete();

    // Verify that the repository method was called
    verify(repository, times(1)).findAllPets();
  }

  @Test
  void testFindAllPetsEmpty() {
    // Mock the repository response
    when(repository.findAllPets()).thenReturn(Flux.empty());

    // Verify the result
    StepVerifier.create(petStore.findAllPets())
        .verifyComplete();

    // Verify that the repository method was called
    verify(repository, times(1)).findAllPets();
  }

  // Add more tests here...

  @Test
  void testGetPetById() {
    // Mock the repository response
    Pet pet = new Pet();
    pet.setId(1L);
    pet.setName("Max");
    when(repository.getPetById(1L)).thenReturn(Mono.just(pet));

    // Verify the result
    StepVerifier.create(petStore.getPetById(1L))
        .expectNext(pet)
        .verifyComplete();

    // Verify that the repository method was called
    verify(repository, times(1)).getPetById(1L);
  }

  @Test
  void testGetPetsByStatus() {
    // Mock the repository response
    Pet pet1 = new Pet();
    pet1.setId(1L);
    pet1.setName("Max");
    pet1.setStatus(Pet.StatusEnum.AVAILABLE);
    Pet pet2 = new Pet();
    pet2.setId(2L);
    pet2.setName("Bella");
    pet2.setStatus(Pet.StatusEnum.PENDING);
    when(repository.getPetsByStatus(Collections.singletonList("available"))).thenReturn(Flux.just(pet1));

    // Verify the result
    StepVerifier.create(petStore.getPetsByStatus(Collections.singletonList("available")))
        .expectNext(pet1)
        .verifyComplete();

    // Verify that the repository method was called
    verify(repository, times(1)).getPetsByStatus(Collections.singletonList("available"));
  }

  @Test
  void testGetPetsByTags() {
    // Mock the repository response
    Pet pet1 = new Pet();
    pet1.setId(1L);
    pet1.setName("Max");
    Tag tag = new Tag();
    tag.setId(1L);
    tag.setName("dog");

    pet1.setTags(Arrays.asList(tag));
    Pet pet2 = new Pet();
    pet2.setId(2L);
    pet2.setName("Bella");
    Tag catTag = new Tag();
    catTag.setId(2L);
    catTag.setName("cat");
    pet2.setTags(Arrays.asList(catTag));
    when(repository.getPetsByTags(Collections.singletonList("dog"))).thenReturn(Flux.just(pet1));

    // Verify the result
    StepVerifier.create(petStore.getPetsByTags(Collections.singletonList("dog")))
        .expectNext(pet1)
        .verifyComplete();

    // Verify that the repository method was called
    verify(repository, times(1)).getPetsByTags(Collections.singletonList("dog"));
  }
}
