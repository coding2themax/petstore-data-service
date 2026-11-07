package com.coding2themax.petstore.data.support;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * One DynamoDB Local container shared by every test in the run. It is started on first use and
 * stopped by Testcontainers when the JVM exits.
 */
public final class DynamoDbLocalContainer {

  private static final int PORT = 8000;

  @SuppressWarnings("resource")
  private static final GenericContainer<?> CONTAINER = new GenericContainer<>(
      DockerImageName.parse("amazon/dynamodb-local:3.3.1"))
      .withExposedPorts(PORT);

  private DynamoDbLocalContainer() {
  }

  public static synchronized String endpoint() {
    if (!CONTAINER.isRunning()) {
      CONTAINER.start();
    }
    return "http://" + CONTAINER.getHost() + ":" + CONTAINER.getMappedPort(PORT);
  }
}
