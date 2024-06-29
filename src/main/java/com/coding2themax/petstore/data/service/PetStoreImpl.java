package com.coding2themax.petstore.data.service;

import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Pet.StatusEnum;
import org.springframework.stereotype.Service;

import com.coding2themax.petstore.data.repo.PetRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PetStoreImpl implements PetService {

  private PetRepository repository;

  public PetStoreImpl(PetRepository repository) {
    this.repository = repository;
  }

  @Override
  public Flux<Pet> findAllPets() {

    return repository.findAllPets();
  }

  @Override
  public Mono<Pet> getPetById(Long id) {
    return repository.getPetById(id);
  }

  @Override
  public Flux<Pet> getPetsByStatus(StatusEnum status) {
    return repository.getPetsByStatus(status.getValue());
  }

}
