package com.coding2themax.petstore.data.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * Test slice for the DynamoDB data layer: loads only the DynamoDB client, table setup, sample
 * data and repositories (no web layer), against a DynamoDB Local container. All tests using it
 * share one application context and one container.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest(classes = DynamoDbTestConfiguration.class, webEnvironment = WebEnvironment.NONE, properties = {
    "petstore.dynamodb.region=us-east-1",
    "petstore.dynamodb.table-prefix=test-",
    "petstore.dynamodb.access-key-id=test",
    "petstore.dynamodb.secret-access-key=test",
    "petstore.dynamodb.create-tables=true",
    "petstore.dynamodb.seed-data=true" })
public @interface DynamoDbTest {
}
