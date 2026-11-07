package com.coding2themax.petstore.data.repo;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class HorseR2DBCService implements HorseRepository {

  private static final String HORSE_QUERY = """
      select h.horseid, h.name, h.breed, h.age, h.owner,
             f.scheduleid, f.feedtime, f.feedtype, f.quantity, f.unit, f.notes
        from petstore.horse h
        left join petstore.feeding_schedule f on f.horseid = h.horseid
      """;

  private static final String ORDER_BY = " order by h.horseid, f.feedtime, f.scheduleid";

  private static final String SCHEDULE_COLUMNS = "scheduleid, horseid, feedtime, feedtype, quantity, unit, notes";

  private final DatabaseClient client;

  public HorseR2DBCService(DatabaseClient client) {
    this.client = client;
  }

  @Override
  public Flux<Horse> findAllHorses() {
    return client.sql(HORSE_QUERY + ORDER_BY).fetch().all()
        .bufferUntilChanged(row -> row.get("horseid"))
        .map(HorseMapper::toHorseFromRows);
  }

  @Override
  public Mono<Horse> getHorseById(Long id) {
    return client.sql(HORSE_QUERY + " where h.horseid = :id" + ORDER_BY)
        .bind("id", id)
        .fetch().all()
        .collectList()
        .filter(rows -> !rows.isEmpty())
        .map(HorseMapper::toHorseFromRows);
  }

  @Override
  @Transactional
  public Mono<Horse> createHorse(Horse horse) {
    GenericExecuteSpec insert = client.sql("""
        insert into petstore.horse (name, breed, age, owner)
        values (:name, :breed, :age, :owner)
        returning horseid
        """);
    return bindHorse(insert, horse)
        .map(row -> row.get("horseid", Long.class))
        .one()
        .flatMap(id -> insertSchedules(id, horse)
            .collectList()
            .map(schedules -> horse.withId(id).withFeedingSchedules(schedules)));
  }

  @Override
  @Transactional
  public Mono<Horse> updateHorse(Long id, Horse horse) {
    GenericExecuteSpec update = client.sql("""
        update petstore.horse
           set name = :name, breed = :breed, age = :age, owner = :owner
         where horseid = :id
        """).bind("id", id);
    return bindHorse(update, horse)
        .fetch().rowsUpdated()
        .filter(count -> count > 0)
        .flatMap(count -> client.sql("delete from petstore.feeding_schedule where horseid = :id")
            .bind("id", id)
            .fetch().rowsUpdated()
            .thenMany(insertSchedules(id, horse))
            .collectList()
            .map(schedules -> horse.withId(id).withFeedingSchedules(schedules)));
  }

  @Override
  public Mono<Boolean> deleteHorse(Long id) {
    // feeding schedules are removed by the "on delete cascade" foreign key
    return client.sql("delete from petstore.horse where horseid = :id")
        .bind("id", id)
        .fetch().rowsUpdated()
        .map(count -> count > 0);
  }

  @Override
  public Flux<FeedingSchedule> getFeedingSchedules(Long horseId) {
    return client.sql("select " + SCHEDULE_COLUMNS
        + " from petstore.feeding_schedule where horseid = :horseId order by feedtime, scheduleid")
        .bind("horseId", horseId)
        .fetch().all()
        .map(HorseMapper::toFeedingSchedule);
  }

  @Override
  public Mono<FeedingSchedule> addFeedingSchedule(Long horseId, FeedingSchedule schedule) {
    GenericExecuteSpec insert = client.sql("""
        insert into petstore.feeding_schedule (horseid, feedtime, feedtype, quantity, unit, notes)
        values (:horseId, :feedTime, :feedType, :quantity, :unit, :notes)
        returning scheduleid
        """)
        .bind("horseId", horseId)
        .bind("feedTime", schedule.feedingTime())
        .bind("feedType", schedule.feedType())
        .bind("quantity", schedule.quantity())
        .bind("unit", schedule.unit());
    return bindNullable(insert, "notes", schedule.notes(), String.class)
        .map(row -> row.get("scheduleid", Long.class))
        .one()
        .map(id -> schedule.withIds(id, horseId));
  }

  @Override
  public Mono<Boolean> deleteFeedingSchedule(Long horseId, Long scheduleId) {
    return client.sql("delete from petstore.feeding_schedule where horseid = :horseId and scheduleid = :scheduleId")
        .bind("horseId", horseId)
        .bind("scheduleId", scheduleId)
        .fetch().rowsUpdated()
        .map(count -> count > 0);
  }

  private Flux<FeedingSchedule> insertSchedules(Long horseId, Horse horse) {
    return Flux.fromIterable(horse.feedingSchedules())
        .concatMap(schedule -> addFeedingSchedule(horseId, schedule));
  }

  private static GenericExecuteSpec bindHorse(GenericExecuteSpec spec, Horse horse) {
    spec = spec.bind("name", horse.name());
    spec = bindNullable(spec, "breed", horse.breed(), String.class);
    spec = bindNullable(spec, "age", horse.age(), Integer.class);
    return bindNullable(spec, "owner", horse.owner(), String.class);
  }

  private static GenericExecuteSpec bindNullable(GenericExecuteSpec spec, String name, Object value,
      Class<?> type) {
    return value == null ? spec.bindNull(name, type) : spec.bind(name, value);
  }
}
