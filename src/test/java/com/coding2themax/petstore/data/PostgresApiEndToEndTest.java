package com.coding2themax.petstore.data;

import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;

import com.coding2themax.petstore.data.repo.HorseR2DBCService;
import com.coding2themax.petstore.data.repo.PetR2DBCService;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

/** The whole application with the default storage, Postgres (Testcontainers), over HTTP. */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PostgresApiEndToEndTest extends ApiEndToEndContract {

  @Override
  protected List<Class<?>> expectedRepositories() {
    return List.of(PetR2DBCService.class, HorseR2DBCService.class);
  }

  @Override
  protected Class<?> otherStorageBean() {
    return DynamoDbAsyncClient.class;
  }
}
