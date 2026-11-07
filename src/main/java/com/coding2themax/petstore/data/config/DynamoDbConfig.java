package com.coding2themax.petstore.data.config;

import java.net.URI;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DynamoDbProperties.class)
public class DynamoDbConfig {

  @Bean
  public DynamoDbAsyncClient dynamoDbAsyncClient(DynamoDbProperties properties) {
    DynamoDbAsyncClientBuilder builder = DynamoDbAsyncClient.builder().region(Region.of(properties.region()));
    if (properties.hasEndpoint()) {
      builder.endpointOverride(URI.create(properties.endpoint()));
    }
    if (properties.hasStaticCredentials()) {
      builder.credentialsProvider(StaticCredentialsProvider.create(
          AwsBasicCredentials.create(properties.accessKeyId(), properties.secretAccessKey())));
    }
    return builder.build();
  }

  @Bean
  public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(DynamoDbAsyncClient client) {
    return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(client).build();
  }
}
