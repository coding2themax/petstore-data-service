package com.coding2themax.petstore.data.service;

import java.util.List;

import org.openapitools.client.model.Pet;

import com.coding2themax.petstore.data.exception.StatusNotFoundException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PetService {

  /*
   * Mono<Pet> createPet(Pet pet);
   * Mono<Pet> getPetById(Long id);
   * 
   * Mono<Pet> updatePet(Pet pet);
   * 
   * Mono<Void> deletePet(Long id);
   * 
   * Flux<Pet> findPetsByStatus(String status);
   */
  Flux<Pet> getPetsByTags(List<String> tags);

  Flux<Pet> getPetsByStatus(List<String> status);

  Flux<Pet> findAllPets();

  Mono<Pet> getPetById(Long id);
}
