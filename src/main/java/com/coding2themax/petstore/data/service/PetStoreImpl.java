package com.coding2themax.petstore.data.service;

import java.util.List;
import java.util.Optional;

import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Tag;
import org.springframework.stereotype.Service;

import com.coding2themax.petstore.data.exception.StatusNotFoundException;
import com.coding2themax.petstore.data.exception.TagNotFoundException;
import com.coding2themax.petstore.data.repo.PetRepository;
import com.coding2themax.petstore.data.repo.TagRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PetStoreImpl implements PetService {

  private PetRepository repository;
  private TagRepository tagRepository;

  public PetStoreImpl(PetRepository repository, TagRepository tagRepository) {
    this.repository = repository;
    this.tagRepository = tagRepository;
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
  public Flux<Pet> getPetsByStatus(List<String> status) {
    return repository.getPetsByStatus(status)
        .switchIfEmpty(Mono.error(new StatusNotFoundException("Invalid status value")));
  }

  @Override
  public Flux<Pet> getPetsByTags(List<String> tags) {

    return repository.getPetsByTags(tags).switchIfEmpty(Mono.error(new TagNotFoundException("Invalid tag value")));

  }

  @Override
  public Mono<Pet> createPet(Pet pet) {
    // TODO Auto-generated method stub

    // Find the tags in the database
    List<Tag> tagNames = Optional.ofNullable(pet.getTags()).orElse(List.of());
    if (!tagNames.isEmpty()) {
      return Flux.fromIterable(tagNames).flatMap(tag -> tagRepository.createTag(tag))
          .collectList().flatMap(tags -> {
            pet.setTags(tags);
            return repository.createPet(pet);
          });
    } else {
      return repository.createPet(pet);
    }

    // throw new UnsupportedOperationException("Unimplemented method 'createPet'");
  }

}
