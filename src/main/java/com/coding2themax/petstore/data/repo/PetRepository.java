package com.coding2themax.petstore.data.repo;

import org.openapitools.client.model.Pet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PetRepository {

  Flux<Pet> findAllPets();

  Mono<Pet> getPetById(Long id);

}
