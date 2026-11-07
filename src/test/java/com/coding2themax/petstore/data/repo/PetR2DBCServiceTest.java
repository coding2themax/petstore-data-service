package com.coding2themax.petstore.data.repo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;

/**
 * Runs the pet repository contract against Postgres, using the R2DBC test slice: only the R2DBC
 * infrastructure, schema.sql/data.sql initialization and transactions are loaded, against a
 * Testcontainers Postgres database.
 */
@DataR2dbcTest
@Import(PetR2DBCService.class)
class PetR2DBCServiceTest extends PetRepositoryContract {

  @Autowired
  private PetR2DBCService repository;

  @Override
  protected PetRepository repository() {
    return repository;
  }

  @Test
  void mapperCanBeCreated() {
    Assertions.assertNotNull(new PetMapper());
  }
}
