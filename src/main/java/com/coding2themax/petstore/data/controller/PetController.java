package com.coding2themax.petstore.data.controller;

import org.openapitools.client.model.Pet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coding2themax.petstore.data.service.PetService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/pets")
public class PetController {

  private PetService service;

  public PetController(PetService service) {
    this.service = service;
  }

  @GetMapping("/all")
  public Flux<Pet> getAllPets() {
    return service.findAllPets();
  }
}
