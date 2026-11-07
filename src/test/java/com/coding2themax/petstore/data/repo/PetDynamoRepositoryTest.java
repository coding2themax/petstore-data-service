package com.coding2themax.petstore.data.repo;

import org.springframework.beans.factory.annotation.Autowired;

import com.coding2themax.petstore.data.support.DynamoDbTest;

/** Runs the pet repository contract against DynamoDB Local. */
@DynamoDbTest
class PetDynamoRepositoryTest extends PetRepositoryContract {

  @Autowired
  private PetDynamoRepository repository;

  @Override
  protected PetRepository repository() {
    return repository;
  }
}
