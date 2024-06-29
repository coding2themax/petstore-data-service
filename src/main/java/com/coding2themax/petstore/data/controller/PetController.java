package com.coding2themax.petstore.data.controller;

import org.openapitools.client.model.Pet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coding2themax.petstore.data.service.PetService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pet")
public class PetController {

  private PetService service;

  public PetController(PetService service) {
    this.service = service;
  }

  @GetMapping("/all")
  public Flux<Pet> getAllPets() {
    return service.findAllPets();
  }

  @GetMapping("/{petId}")
  public Mono<Pet> getPetById(@PathVariable(value = "petId") Long petId) {
    return service.getPetById(petId);
  }

  @GetMapping("/status/{status}")
  public Flux<Pet> getPetsByStatus(@PathVariable(value = "status") String status) {
    return service.getPetsByStatus(Pet.StatusEnum.fromValue(status));
  }
}
