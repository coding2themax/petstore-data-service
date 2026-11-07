package com.coding2themax.petstore.data.repo;

import org.springframework.beans.factory.annotation.Autowired;

import com.coding2themax.petstore.data.support.DynamoDbTest;

/** Runs the horse repository contract against DynamoDB Local. */
@DynamoDbTest
class HorseDynamoRepositoryTest extends HorseRepositoryContract {

  @Autowired
  private HorseDynamoRepository repository;

  @Override
  protected HorseRepository repository() {
    return repository;
  }
}
