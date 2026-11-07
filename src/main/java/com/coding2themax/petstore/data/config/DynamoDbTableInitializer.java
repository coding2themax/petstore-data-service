package com.coding2themax.petstore.data.config;

import static com.coding2themax.petstore.data.config.DynamoDbProperties.COUNTER_TABLE;
import static com.coding2themax.petstore.data.config.DynamoDbProperties.HORSE_TABLE;
import static com.coding2themax.petstore.data.config.DynamoDbProperties.PET_STATUS_INDEX;
import static com.coding2themax.petstore.data.config.DynamoDbProperties.PET_TABLE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

/**
 * Creates the DynamoDB tables if they are missing. Meant for local development and tests; in AWS
 * the tables should be created by infrastructure code with the same definitions.
 */
@Component
@Order(0)
@ConditionalOnBooleanProperty("petstore.dynamodb.create-tables")
@ConditionalOnDynamoDbStorage
public class DynamoDbTableInitializer implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(DynamoDbTableInitializer.class);

  private final DynamoDbAsyncClient client;
  private final DynamoDbProperties properties;

  public DynamoDbTableInitializer(DynamoDbAsyncClient client, DynamoDbProperties properties) {
    this.client = client;
    this.properties = properties;
  }

  @Override
  public void run(ApplicationArguments args) {
    Flux.fromIterable(tableDefinitions()).concatMap(this::createIfMissing).blockLast();
  }

  List<CreateTableRequest> tableDefinitions() {
    return List.of(
        CreateTableRequest.builder()
            .tableName(properties.tableName(PET_TABLE))
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .attributeDefinitions(
                attribute("petId", ScalarAttributeType.N),
                attribute("status", ScalarAttributeType.S))
            .keySchema(hashKey("petId"))
            .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                .indexName(PET_STATUS_INDEX)
                .keySchema(hashKey("status"))
                .projection(p -> p.projectionType(ProjectionType.ALL))
                .build())
            .build(),
        simpleTable(HORSE_TABLE, "horseId", ScalarAttributeType.N),
        simpleTable(COUNTER_TABLE, "name", ScalarAttributeType.S));
  }

  private Mono<Void> createIfMissing(CreateTableRequest request) {
    String table = request.tableName();
    return Mono.fromFuture(() -> client.describeTable(r -> r.tableName(table)))
        .then()
        .onErrorResume(ResourceNotFoundException.class, missing -> {
          log.info("Creating DynamoDB table {}", table);
          return Mono.fromFuture(() -> client.createTable(request))
              .then(Mono.fromFuture(() -> client.waiter().waitUntilTableExists(r -> r.tableName(table))))
              .then();
        })
        // another instance created it at the same time
        .onErrorResume(ResourceInUseException.class, inUse -> Mono.empty());
  }

  private CreateTableRequest simpleTable(String table, String key, ScalarAttributeType type) {
    return CreateTableRequest.builder()
        .tableName(properties.tableName(table))
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .attributeDefinitions(attribute(key, type))
        .keySchema(hashKey(key))
        .build();
  }

  private static AttributeDefinition attribute(String name, ScalarAttributeType type) {
    return AttributeDefinition.builder().attributeName(name).attributeType(type).build();
  }

  private static KeySchemaElement hashKey(String name) {
    return KeySchemaElement.builder().attributeName(name).keyType(KeyType.HASH).build();
  }
}
