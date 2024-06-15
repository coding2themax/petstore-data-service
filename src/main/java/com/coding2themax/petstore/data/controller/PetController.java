package com.coding2themax.petstore.data.controller;

import java.util.List;

import org.openapitools.client.model.Pet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coding2themax.petstore.data.repo.PetRepository;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/pets")
public class PetController {

  private PetRepository repository;

  public PetController(PetRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/all")
  public Flux<Pet> getAllPets() {
    return repository.findAllPets();
  }
}
