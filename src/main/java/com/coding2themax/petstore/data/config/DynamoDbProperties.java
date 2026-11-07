package com.coding2themax.petstore.data.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DynamoDB settings, bound from {@code petstore.dynamodb.*}.
 *
 * @param region          AWS region of the tables
 * @param endpoint        endpoint override, e.g. a DynamoDB Local URL; blank uses AWS
 * @param tablePrefix     prefix added to every table name, e.g. {@code petstore-}
 * @param accessKeyId     optional static access key (for DynamoDB Local); blank uses the
 *                        default AWS credentials chain
 * @param secretAccessKey optional static secret key, used with {@code accessKeyId}
 * @param createTables    create missing tables at startup (local development and tests)
 * @param seedData        load sample pets and horses at startup (local development and tests)
 */
@ConfigurationProperties("petstore.dynamodb")
public record DynamoDbProperties(
    String region,
    String endpoint,
    String tablePrefix,
    String accessKeyId,
    String secretAccessKey,
    boolean createTables,
    boolean seedData) {

  public static final String PET_TABLE = "pet";
  public static final String HORSE_TABLE = "horse";
  public static final String COUNTER_TABLE = "counter";
  public static final String PET_STATUS_INDEX = "status-index";

  public DynamoDbProperties {
    region = isBlank(region) ? "us-east-1" : region;
    tablePrefix = tablePrefix == null ? "petstore-" : tablePrefix;
  }

  public String tableName(String table) {
    return tablePrefix + table;
  }

  public boolean hasEndpoint() {
    return !isBlank(endpoint);
  }

  public boolean hasStaticCredentials() {
    return !isBlank(accessKeyId) && !isBlank(secretAccessKey);
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
