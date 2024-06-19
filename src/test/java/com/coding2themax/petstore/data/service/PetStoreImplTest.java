package com.coding2themax.petstore.data.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openapitools.client.model.Pet;

import com.coding2themax.petstore.data.repo.PetRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

public class PetStoreImplTest {

  @Mock
  private PetRepository repository;

  private PetStoreImpl petStore;

  public PetStoreImplTest() {
    MockitoAnnotations.openMocks(this);
    petStore = new PetStoreImpl(repository);
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

}