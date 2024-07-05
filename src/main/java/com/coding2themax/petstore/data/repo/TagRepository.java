package com.coding2themax.petstore.data.repo;

import org.openapitools.client.model.Tag;

import reactor.core.publisher.Mono;

public interface TagRepository {

  Mono<Tag> getTagById(Long id);

  Mono<Tag> createTag(Tag tag);

}