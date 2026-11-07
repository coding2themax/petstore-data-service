package com.coding2themax.petstore.data.repo;

import java.util.List;

import org.openapitools.client.model.Pet;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.coding2themax.petstore.data.config.ConditionalOnPostgresStorage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@ConditionalOnPostgresStorage
public class PetR2DBCService implements PetRepository {

  private final DatabaseClient client;

  private static final String QUERY = """
      select p.petid, p.name,p.category, c.name as category_name, ph.photourl, t.tagid, t.tagname, p.petstatus
        FROM petstore.pet p
        join petstore.category c on c.catid = p.category
        join    petstore.photo ph on ph.petid = p.petid
        join petstore.tag as t on t.petid = p.petid
            """;

  // rows of one pet must be next to each other for bufferUntilChanged to group them
  private static final String ORDER_BY = " order by p.petid, t.tagid, ph.photoid";

  public PetR2DBCService(DatabaseClient client) {
    this.client = client;
  }

  @Override
  public Flux<Pet> findAllPets() {
    return toPets(client.sql(QUERY + ORDER_BY));
  }

  @Override
  public Mono<Pet> getPetById(Long id) {
    return toPets(client.sql(QUERY + " where p.petid = :id" + ORDER_BY).bind("id", id)).singleOrEmpty();
  }

  @Override
  public Flux<Pet> getPetsByStatus(List<String> petstatus) {
    if (petstatus.isEmpty()) {
      return Flux.empty();
    }
    return toPets(client.sql(QUERY + " where p.petstatus::text = any(:statuses)" + ORDER_BY)
        .bind("statuses", petstatus.toArray(String[]::new)));
  }

  @Override
  public Flux<Pet> getPetsByTags(List<String> tags) {
    if (tags.isEmpty()) {
      return Flux.empty();
    }
    // keep every tag of a matching pet, not only the tags that matched
    return toPets(client.sql(QUERY
        + " where p.petid in (select petid from petstore.tag where tagname = any(:tags))" + ORDER_BY)
        .bind("tags", tags.toArray(String[]::new)));
  }

  private static Flux<Pet> toPets(DatabaseClient.GenericExecuteSpec query) {
    return query.fetch().all()
        .bufferUntilChanged(rs -> rs.get("petid"))
        .flatMap(PetMapper::toPetfromRows);
  }
}
