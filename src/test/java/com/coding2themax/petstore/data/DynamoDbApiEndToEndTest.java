package com.coding2themax.petstore.data;

import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistrar;

import com.coding2themax.petstore.data.repo.HorseDynamoRepository;
import com.coding2themax.petstore.data.repo.PetDynamoRepository;
import com.coding2themax.petstore.data.support.DynamoDbLocalContainer;

import io.r2dbc.spi.ConnectionFactory;

/**
 * The whole application with the {@code dynamodb} profile, against DynamoDB Local, over HTTP.
 * Also proves the profile turns the Postgres auto-configuration off.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
    "petstore.dynamodb.table-prefix=e2e-",
    "petstore.dynamodb.access-key-id=test",
    "petstore.dynamodb.secret-access-key=test",
    "petstore.dynamodb.create-tables=true",
    "petstore.dynamodb.seed-data=true" })
@ActiveProfiles("dynamodb")
@AutoConfigureWebTestClient
class DynamoDbApiEndToEndTest extends ApiEndToEndContract {

  @TestConfiguration(proxyBeanMethods = false)
  static class DynamoDbLocal {
    @Bean
    static DynamicPropertyRegistrar dynamoDbLocalEndpoint() {
      return registry -> registry.add("petstore.dynamodb.endpoint", DynamoDbLocalContainer::endpoint);
    }
  }

  @Override
  protected List<Class<?>> expectedRepositories() {
    return List.of(PetDynamoRepository.class, HorseDynamoRepository.class);
  }

  @Override
  protected Class<?> otherStorageBean() {
    return ConnectionFactory.class;
  }
}
