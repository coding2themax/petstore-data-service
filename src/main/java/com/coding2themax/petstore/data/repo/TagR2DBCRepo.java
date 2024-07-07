package com.coding2themax.petstore.data.repo;

import org.openapitools.client.model.Tag;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This class implements the TagRepository interface and provides the
 * implementation for working with tags in a R2DBC-based data service.
 */
@Repository
public class TagR2DBCRepo implements TagRepository {

  private final DatabaseClient client;

  public TagR2DBCRepo(DatabaseClient client) {
    this.client = client;
  }

  private final String QUERY = """
      select t.tagid, t.tagname
        FROM petstore.tag t
            """;

  private final String CREATE_TAG_QUERY = """
      insert into petstore.tag (tagname) values (:tagname)
      """;

  @Override
  public Mono<Tag> getTagById(Long id) {
    return client.sql(QUERY).fetch().all().bufferUntilChanged(rs -> rs.get("tagid"))
        .flatMap(TagMapper::toTagfromRows).filter(tag -> tag.getId().equals(id)).singleOrEmpty();
  }

  @Override
  public Mono<Tag> createTag(Tag tag) {
    return client.sql(CREATE_TAG_QUERY).bind("tagname", tag.getName()).fetch().rowsUpdated().map(count -> tag);
  }

  @Override
  public Flux<Tag> getTags() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getTags'");
  }

  @Override
  public Flux<Tag> getTagsByPetId(Long petId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getTagsByPetId'");
  }

  @Override
  public Flux<Tag> getTagsByTagName(String tagName) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getTagsByTagName'");
  }

}
