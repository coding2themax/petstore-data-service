package com.coding2themax.petstore.data.repo;

import static com.coding2themax.petstore.data.config.DynamoDbProperties.COUNTER_TABLE;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.coding2themax.petstore.data.config.DynamoDbProperties;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;

/**
 * Hands out increasing numeric ids from atomic counters in the counter table, because DynamoDB
 * has no auto-increment columns.
 */
@Component
public class IdGenerator {

  public static final String HORSE = "horse";
  public static final String FEEDING_SCHEDULE = "feedingSchedule";

  private static final String VALUE = "currentValue";

  private final DynamoDbAsyncClient client;
  private final String table;

  public IdGenerator(DynamoDbAsyncClient client, DynamoDbProperties properties) {
    this.client = client;
    this.table = properties.tableName(COUNTER_TABLE);
  }

  /** Atomically increments the named counter and returns the new value. */
  public Mono<Long> next(String counter) {
    return Mono.fromFuture(() -> client.updateItem(r -> r.tableName(table)
        .key(key(counter))
        .updateExpression("ADD #v :one")
        .expressionAttributeNames(Map.of("#v", VALUE))
        .expressionAttributeValues(Map.of(":one", AttributeValue.fromN("1")))
        .returnValues(ReturnValue.UPDATED_NEW)))
        .map(response -> Long.parseLong(response.attributes().get(VALUE).n()));
  }

  /** Starts the named counter at {@code value} unless it already exists. */
  public Mono<Void> initialize(String counter, long value) {
    return Mono.fromFuture(() -> client.updateItem(r -> r.tableName(table)
        .key(key(counter))
        .updateExpression("SET #v = if_not_exists(#v, :start)")
        .expressionAttributeNames(Map.of("#v", VALUE))
        .expressionAttributeValues(Map.of(":start", AttributeValue.fromN(Long.toString(value))))))
        .then();
  }

  private static Map<String, AttributeValue> key(String counter) {
    return Map.of("name", AttributeValue.fromS(counter));
  }
}
