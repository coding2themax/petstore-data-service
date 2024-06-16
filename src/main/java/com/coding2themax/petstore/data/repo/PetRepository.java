package com.coding2themax.petstore.data.repo;

import org.openapitools.client.model.Pet;

import reactor.core.publisher.Flux;

public interface PetRepository {

  Flux<Pet> findAllPets();

}
