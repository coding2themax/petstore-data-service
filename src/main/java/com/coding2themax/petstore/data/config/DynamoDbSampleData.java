package com.coding2themax.petstore.data.config;

import static com.coding2themax.petstore.data.config.DynamoDbProperties.HORSE_TABLE;
import static com.coding2themax.petstore.data.config.DynamoDbProperties.PET_TABLE;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.openapitools.client.model.Category;
import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Tag;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;
import com.coding2themax.petstore.data.repo.HorseItem;
import com.coding2themax.petstore.data.repo.IdGenerator;
import com.coding2themax.petstore.data.repo.PetItem;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

/**
 * Loads sample pets and horses (the same data as postgres/init.sql). Items that
 * already exist are left alone, so restarting does not undo changes.
 */
@Component
@Order(1)
@ConditionalOnBooleanProperty("petstore.dynamodb.seed-data")
@ConditionalOnDynamoDbStorage
public class DynamoDbSampleData implements ApplicationRunner {

  private final DynamoDbEnhancedAsyncClient client;
  private final DynamoDbProperties properties;
  private final IdGenerator ids;

  public DynamoDbSampleData(DynamoDbEnhancedAsyncClient client, DynamoDbProperties properties, IdGenerator ids) {
    this.client = client;
    this.properties = properties;
    this.ids = ids;
  }

  @Override
  public void run(ApplicationArguments args) {
    var pets = client.table(properties.tableName(PET_TABLE), TableSchema.fromBean(PetItem.class));
    var horses = client.table(properties.tableName(HORSE_TABLE), TableSchema.fromBean(HorseItem.class));
    Expression petIsNew = Expression.builder()
        .expression("attribute_not_exists(#id)")
        .expressionNames(Map.of("#id", "petId"))
        .build();

    Flux<Void> petWrites = Flux.fromIterable(pets()).map(PetItem::from)
        .concatMap(pet -> ignoreExisting(Mono.fromFuture(() -> pets.putItem(PutItemEnhancedRequest.builder(PetItem.class)
            .item(pet).conditionExpression(petIsNew).build()))));
    // horse items are versioned, so a put with no version only succeeds if the horse is new
    Flux<Void> horseWrites = Flux.fromIterable(horses()).map(HorseItem::from)
        .concatMap(horse -> ignoreExisting(Mono.fromFuture(() -> horses.putItem(horse))));

    petWrites.thenMany(horseWrites)
        .then(ids.initialize(IdGenerator.HORSE, 2))
        .then(ids.initialize(IdGenerator.FEEDING_SCHEDULE, 3))
        .block();
  }

  private static Mono<Void> ignoreExisting(Mono<Void> write) {
    return write.onErrorResume(ConditionalCheckFailedException.class, e -> Mono.empty());
  }

  static List<Pet> pets() {
    Category fish = new Category().id(1L).name("Fish");
    return List.of(
        new Pet().id(1L).name("Angelfish").category(fish)
            .photoUrls(List.of("http://www.jpetstore.com/images/fish1.gif", "http://www.jpetstore.com/images/fish2.gif"))
            .tags(List.of(new Tag().id(1L).name("tag1"), new Tag().id(2L).name("tag2")))
            .status(Pet.StatusEnum.AVAILABLE),
        new Pet().id(2L).name("Tiger Shark").category(fish)
            .photoUrls(List.of("http://www.jpetstore.com/images/fish3.gif"))
            .tags(List.of(new Tag().id(3L).name("tag3")))
            .status(Pet.StatusEnum.AVAILABLE));
  }

  static List<Horse> horses() {
    return List.of(
        new Horse(1L, "Thunder", "Thoroughbred", 7, "Alex", List.of(
            new FeedingSchedule(1L, 1L, LocalTime.of(7, 0), "Hay", new BigDecimal("5"), "kg", "Morning feed"),
            new FeedingSchedule(2L, 1L, LocalTime.of(18, 0), "Oats", new BigDecimal("2.5"), "kg", null))),
        new Horse(2L, "Daisy", "Quarter Horse", 12, "Sam", List.of(
            new FeedingSchedule(3L, 2L, LocalTime.of(8, 0), "Hay", new BigDecimal("4"), "kg", null))));
  }
}
