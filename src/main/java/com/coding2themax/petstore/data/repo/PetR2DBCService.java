package com.coding2themax.petstore.data.repo;

import java.util.List;

import org.openapitools.client.model.Pet;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PetR2DBCService implements PetRepository {

  private final DatabaseClient client;

  private final String QUERY = """
          select p.petid, p.name,p.category, c.name as category_name, ph.photourl, t.tagid, tg.tagname, p.petstatus
            FROM petstore.pet p
            join petstore.category c on c.catid = p.category
            join    petstore.photo ph on ph.petid = p.petid
            join petstore.pettags as t on t.petid = p.petid
      join petstore.tag as tg on t.tagid = tg.tagid
                """;

  // create pet sql query
  private final String CREATE_PET_QUERY = """
      insert into petstore.pet (name, category, petstatus) values (:name, :category, :petstatus)
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
  public Flux<Pet> getPetsByStatus(List<String> petstatus) {
    if (petstatus.size() == 1) {
      String query = String.format("%s where p.petstatus = \'%s\'", QUERY, petstatus.get(0));
      return client.sql(query).fetch().all()
          .bufferUntilChanged(rs -> rs.get("petid"))
          .flatMap(PetMapper::toPetfromRows);
    } else {
      // if more than one status is provided then use IN clause.

      String statues = petstatus.stream()
          .collect(StringBuilder::new, (sb, s) -> sb.append("\'").append(s).append("\',"),
              StringBuilder::append)
          .toString();
      String fomattedCorrectly = statues.substring(0, statues.length() - 1);
      String query = String.format("%s where p.petstatus in (%s)", QUERY, fomattedCorrectly);
      return client.sql(query).fetch().all()
          .bufferUntilChanged(rs -> rs.get("petid"))
          .flatMap(PetMapper::toPetfromRows);
    }

  }

  @Override
  public Flux<Pet> getPetsByTags(List<String> tags) {
    String tagQuery = tags.stream()
        .collect(StringBuilder::new, (sb, s) -> sb.append("\'").append(s).append("\',"),
            StringBuilder::append)
        .toString();
    String fomattedCorrectly = tagQuery.substring(0, tagQuery.length() - 1);
    String query = String.format("%s where tagname in (%s)", QUERY, fomattedCorrectly);
    return client.sql(query).fetch().all()
        .bufferUntilChanged(rs -> rs.get("petid"))
        .flatMap(PetMapper::toPetfromRows);
  }

  @Override
  public Mono<Pet> createPet(Pet pet) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createPet'");
  }

}
