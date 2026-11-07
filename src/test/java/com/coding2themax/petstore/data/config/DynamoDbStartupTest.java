package com.coding2themax.petstore.data.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coding2themax.petstore.data.support.DynamoDbTest;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

/** Checks table creation and sample data loading against DynamoDB Local. */
@DynamoDbTest
class DynamoDbStartupTest {

  @Autowired
  private DynamoDbAsyncClient client;

  @Autowired
  private DynamoDbTableInitializer tables;

  @Autowired
  private DynamoDbSampleData sampleData;

  @Test
  void createsAllTablesWithThePrefix() {
    List<String> names = client.listTables().join().tableNames();
    assertEquals(true, names.containsAll(List.of("test-pet", "test-horse", "test-counter")), names.toString());
  }

  @Test
  void petTableHasTheStatusIndex() {
    var index = client.describeTable(r -> r.tableName("test-pet")).join().table().globalSecondaryIndexes().get(0);
    assertEquals(DynamoDbProperties.PET_STATUS_INDEX, index.indexName());
  }

  @Test
  void runningAgainChangesNothing() {
    Integer before = client.scan(r -> r.tableName("test-pet")).join().count();

    tables.run(null);
    sampleData.run(null);

    assertEquals(before, client.scan(r -> r.tableName("test-pet")).join().count());
  }
}
