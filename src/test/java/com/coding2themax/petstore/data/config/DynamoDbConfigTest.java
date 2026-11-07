package com.coding2themax.petstore.data.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

class DynamoDbConfigTest {

  private final DynamoDbConfig config = new DynamoDbConfig();

  @Test
  void defaultsApplyWhenUnset() {
    DynamoDbProperties properties = new DynamoDbProperties(null, null, null, null, null, false, false);

    assertEquals("us-east-1", properties.region());
    assertEquals("petstore-horse", properties.tableName(DynamoDbProperties.HORSE_TABLE));
    assertFalse(properties.hasEndpoint());
    assertFalse(properties.hasStaticCredentials());
  }

  @Test
  void blankValuesCountAsUnset() {
    DynamoDbProperties properties = new DynamoDbProperties(" ", "", "", "key", " ", false, false);

    assertEquals("us-east-1", properties.region());
    assertEquals("pet", properties.tableName(DynamoDbProperties.PET_TABLE));
    assertFalse(properties.hasEndpoint());
    assertFalse(properties.hasStaticCredentials());
    assertFalse(new DynamoDbProperties(null, null, null, " ", "secret", false, false).hasStaticCredentials());
  }

  @Test
  void clientForAws() {
    DynamoDbProperties properties = new DynamoDbProperties("eu-west-1", null, null, null, null, false, false);

    try (DynamoDbAsyncClient client = config.dynamoDbAsyncClient(properties)) {
      assertEquals(Region.EU_WEST_1, client.serviceClientConfiguration().region());
      assertFalse(client.serviceClientConfiguration().endpointOverride().isPresent());
      assertNotNull(config.dynamoDbEnhancedAsyncClient(client));
    }
  }

  @Test
  void clientForDynamoDbLocal() {
    DynamoDbProperties properties = new DynamoDbProperties("us-east-1", "http://localhost:8000", "dev-", "key",
        "secret", true, true);

    assertTrue(properties.hasEndpoint());
    assertTrue(properties.hasStaticCredentials());
    try (DynamoDbAsyncClient client = config.dynamoDbAsyncClient(properties)) {
      assertEquals(URI.create("http://localhost:8000"), client.serviceClientConfiguration().endpointOverride().get());
    }
  }
}
