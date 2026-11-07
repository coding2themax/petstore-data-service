package com.coding2themax.petstore.data.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coding2themax.petstore.data.support.DynamoDbTest;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DynamoDbTest
class IdGeneratorTest {

  @Autowired
  private IdGenerator ids;

  @Test
  void countersStartAtOneAndIncrease() {
    StepVerifier.create(ids.next("test-sequence")).expectNext(1L).verifyComplete();
    StepVerifier.create(ids.next("test-sequence")).expectNext(2L).verifyComplete();
  }

  @Test
  void initializeOnlySetsANewCounter() {
    StepVerifier.create(ids.initialize("test-initialized", 100).then(ids.next("test-initialized")))
        .expectNext(101L)
        .verifyComplete();
    StepVerifier.create(ids.initialize("test-initialized", 5).then(ids.next("test-initialized")))
        .expectNext(102L)
        .verifyComplete();
  }

  @Test
  void concurrentCallsGetDistinctIds() {
    List<Long> values = Flux.range(0, 20).flatMap(i -> ids.next("test-concurrent")).collectList().block();
    assertEquals(20, new HashSet<>(values).size());
  }
}
