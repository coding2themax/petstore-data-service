package com.coding2themax.petstore.data.repo;

import org.openapitools.client.model.Tag;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TagRepository {

  Mono<Tag> getTagById(Long id);

  Mono<Tag> createTag(Tag tag);

  Flux<Tag> getTags();

  Flux<Tag> getTagsByPetId(Long petId);

  Flux<Tag> getTagsByTagName(String tagName);

}