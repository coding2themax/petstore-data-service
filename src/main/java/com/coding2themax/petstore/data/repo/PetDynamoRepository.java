package com.coding2themax.petstore.data.repo;

import static com.coding2themax.petstore.data.config.DynamoDbProperties.PET_STATUS_INDEX;
import static com.coding2themax.petstore.data.config.DynamoDbProperties.PET_TABLE;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openapitools.client.model.Pet;
import org.springframework.stereotype.Repository;

import com.coding2themax.petstore.data.config.DynamoDbProperties;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import com.coding2themax.petstore.data.config.ConditionalOnDynamoDbStorage;

@Repository
@ConditionalOnDynamoDbStorage
public class PetDynamoRepository implements PetRepository {

  private static final Comparator<Pet> BY_ID = Comparator.comparing(Pet::getId);

  private final DynamoDbAsyncTable<PetItem> table;
  private final DynamoDbAsyncIndex<PetItem> statusIndex;

  public PetDynamoRepository(DynamoDbEnhancedAsyncClient client, DynamoDbProperties properties) {
    this.table = client.table(properties.tableName(PET_TABLE), TableSchema.fromBean(PetItem.class));
    this.statusIndex = table.index(PET_STATUS_INDEX);
  }

  @Override
  public Flux<Pet> findAllPets() {
    return Flux.from(table.scan().items()).map(PetItem::toPet).sort(BY_ID);
  }

  @Override
  public Mono<Pet> getPetById(Long id) {
    return Mono.fromFuture(() -> table.getItem(Key.builder().partitionValue(id).build())).map(PetItem::toPet);
  }

  @Override
  public Flux<Pet> getPetsByStatus(List<String> statuses) {
    return Flux.fromIterable(statuses).distinct()
        .flatMap(status -> Flux.from(statusIndex.query(
            QueryConditional.keyEqualTo(k -> k.partitionValue(status)))))
        .flatMapIterable(Page::items)
        .map(PetItem::toPet)
        .sort(BY_ID);
  }

  @Override
  public Flux<Pet> getPetsByTags(List<String> tags) {
    if (tags.isEmpty()) {
      return Flux.empty();
    }
    Map<String, AttributeValue> values = new HashMap<>();
    IntStream.range(0, tags.size()).forEach(i -> values.put(":t" + i, AttributeValue.fromS(tags.get(i))));
    String condition = values.keySet().stream().sorted()
        .map(placeholder -> "contains(#tagNames, " + placeholder + ")")
        .collect(Collectors.joining(" OR "));
    Expression filter = Expression.builder()
        .expression(condition)
        .expressionNames(Map.of("#tagNames", "tagNames"))
        .expressionValues(values)
        .build();
    return Flux.from(table.scan(ScanEnhancedRequest.builder().filterExpression(filter).build()).items())
        .map(PetItem::toPet)
        .sort(BY_ID);
  }
}
