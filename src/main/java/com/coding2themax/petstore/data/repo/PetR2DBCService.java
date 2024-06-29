package com.coding2themax.petstore.data.repo;

import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Pet.StatusEnum;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PetR2DBCService implements PetRepository {

  private final DatabaseClient client;

  private final String QUERY = """
      select p.petid, p.name,p.category, c.name as category_name, ph.photourl, t.tagid, t.tagname, p.petstatus
        FROM petstore.pet p
        join petstore.category c on c.catid = p.category
        join    petstore.photo ph on ph.petid = p.petid
        join petstore.tag as t on t.petid = p.petid
            """;

  public PetR2DBCService(DatabaseClient client) {
    this.client = client;
  }

  @Override
  public Flux<Pet> findAllPets() {

    return client.sql(QUERY).fetch().all().bufferUntilChanged(rs -> rs.get("petid"))
        .flatMap(PetMapper::toPetfromRows);
  }

  @Override
  public Mono<Pet> getPetById(Long id) {
    String query = String.format("%s where p.petid = :id", QUERY);
    return client.sql(query).bind("id",
        id).fetch().all().bufferUntilChanged(rs -> rs.get("petid"))
        .flatMap(PetMapper::toPetfromRows).singleOrEmpty();
  }

  @Override
  public Flux<Pet> getPetsByStatus(String petstatus) {

    String query = String.format("%s where p.petstatus = \'%s\'", QUERY, petstatus);

    return client.sql(query).fetch().all()
        .bufferUntilChanged(rs -> rs.get("petid"))
        .flatMap(PetMapper::toPetfromRows);
  }

}
