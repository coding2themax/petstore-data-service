package com.coding2themax.petstore.data.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistrar;

import com.coding2themax.petstore.data.config.DynamoDbConfig;
import com.coding2themax.petstore.data.config.DynamoDbSampleData;
import com.coding2themax.petstore.data.config.DynamoDbTableInitializer;
import com.coding2themax.petstore.data.repo.HorseDynamoRepository;
import com.coding2themax.petstore.data.repo.IdGenerator;
import com.coding2themax.petstore.data.repo.PetDynamoRepository;

/**
 * The beans of the DynamoDB data layer and nothing else, pointed at DynamoDB Local.
 */
@TestConfiguration(proxyBeanMethods = false)
@Import({ DynamoDbConfig.class, DynamoDbTableInitializer.class, DynamoDbSampleData.class, IdGenerator.class,
    PetDynamoRepository.class, HorseDynamoRepository.class })
public class DynamoDbTestConfiguration {

  @Bean
  static DynamicPropertyRegistrar dynamoDbLocalProperties() {
    return registry -> registry.add("petstore.dynamodb.endpoint", DynamoDbLocalContainer::endpoint);
  }
}
