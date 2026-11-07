package com.coding2themax.petstore.data.repo;

import static com.coding2themax.petstore.data.config.DynamoDbProperties.HORSE_TABLE;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Repository;

import com.coding2themax.petstore.data.config.DynamoDbProperties;
import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

/**
 * Stores each horse as one DynamoDB item with its feeding schedule inside it. Every change is a
 * read-modify-write guarded by the item's version; if another request changed the horse in
 * between, the change is retried on the fresh copy.
 */
@Repository
public class HorseDynamoRepository implements HorseRepository {

  private static final Retry ON_CONFLICT = Retry.backoff(10, Duration.ofMillis(10))
      .filter(ConditionalCheckFailedException.class::isInstance);

  private final DynamoDbAsyncTable<HorseItem> table;
  private final IdGenerator ids;

  public HorseDynamoRepository(DynamoDbEnhancedAsyncClient client, DynamoDbProperties properties, IdGenerator ids) {
    this.table = client.table(properties.tableName(HORSE_TABLE), TableSchema.fromBean(HorseItem.class));
    this.ids = ids;
  }

  @Override
  public Flux<Horse> findAllHorses() {
    return Flux.from(table.scan().items()).map(HorseItem::toHorse).sort(Comparator.comparing(Horse::id));
  }

  @Override
  public Mono<Horse> getHorseById(Long id) {
    return findItem(id).map(HorseItem::toHorse);
  }

  @Override
  public Mono<Horse> createHorse(Horse horse) {
    // a null version makes the enhanced client write only if the id is not taken
    return ids.next(IdGenerator.HORSE)
        .flatMap(id -> withNewFeedingIds(id, horse.feedingSchedules())
            .map(schedules -> HorseItem.from(horse.withId(id).withFeedingSchedules(schedules))))
        .flatMap(this::save)
        .map(HorseItem::toHorse);
  }

  @Override
  public Mono<Horse> updateHorse(Long id, Horse horse) {
    return withNewFeedingIds(id, horse.feedingSchedules())
        .flatMap(schedules -> modify(id, existing -> {
          HorseItem updated = HorseItem.from(horse.withId(id).withFeedingSchedules(schedules));
          updated.setVersion(existing.getVersion());
          return updated;
        }))
        .map(HorseItem::toHorse);
  }

  @Override
  public Mono<Boolean> deleteHorse(Long id) {
    return Mono.fromFuture(() -> table.deleteItem(key(id))).map(deleted -> true).defaultIfEmpty(false);
  }

  @Override
  public Flux<FeedingSchedule> getFeedingSchedules(Long horseId) {
    return findItem(horseId).flatMapIterable(HorseItem::toFeedingSchedules);
  }

  @Override
  public Mono<FeedingSchedule> addFeedingSchedule(Long horseId, FeedingSchedule schedule) {
    return ids.next(IdGenerator.FEEDING_SCHEDULE)
        .map(id -> schedule.withIds(id, horseId))
        .flatMap(added -> modify(horseId, item -> {
          item.addFeedingSchedule(added);
          return item;
        }).map(saved -> added));
  }

  @Override
  public Mono<Boolean> deleteFeedingSchedule(Long horseId, Long scheduleId) {
    return Mono.defer(() -> findItem(horseId)
        .flatMap(item -> item.removeFeedingSchedule(scheduleId) ? save(item).thenReturn(true) : Mono.just(false)))
        .retryWhen(ON_CONFLICT)
        .defaultIfEmpty(false);
  }

  /** Reads the horse, applies the change and saves it, retrying on a concurrent update. */
  private Mono<HorseItem> modify(Long id, Function<HorseItem, HorseItem> change) {
    return Mono.defer(() -> findItem(id).map(change).flatMap(this::save)).retryWhen(ON_CONFLICT);
  }

  private Mono<HorseItem> save(HorseItem item) {
    return Mono.fromFuture(() -> table.putItem(item)).thenReturn(item);
  }

  private Mono<HorseItem> findItem(Long id) {
    return Mono.fromFuture(() -> table.getItem(key(id)));
  }

  private Mono<List<FeedingSchedule>> withNewFeedingIds(Long horseId, List<FeedingSchedule> schedules) {
    return Flux.fromIterable(schedules)
        .concatMap(s -> ids.next(IdGenerator.FEEDING_SCHEDULE).map(id -> s.withIds(id, horseId)))
        .collectList();
  }

  private static Key key(Long id) {
    return Key.builder().partitionValue(id).build();
  }
}
