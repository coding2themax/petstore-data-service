package com.coding2themax.petstore.data.repo;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.coding2themax.petstore.data.model.FeedingSchedule;
import com.coding2themax.petstore.data.model.Horse;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;

/**
 * A horse as stored in the DynamoDB horse table. The feeding schedule is stored inside the horse
 * item, and {@code version} gives optimistic locking so concurrent changes are not lost.
 */
@DynamoDbBean
public class HorseItem {

  private static final Comparator<FeedingSchedule> FEEDING_ORDER = Comparator
      .comparing(FeedingSchedule::feedingTime)
      .thenComparing(FeedingSchedule::id);

  private Long horseId;
  private String name;
  private String breed;
  private Integer age;
  private String owner;
  private List<FeedingItem> feedingSchedules = new ArrayList<>();
  private Long version;

  /** Builds an item from a horse whose id and feeding ids are already set. */
  public static HorseItem from(Horse horse) {
    HorseItem item = new HorseItem();
    item.horseId = horse.id();
    item.name = horse.name();
    item.breed = horse.breed();
    item.age = horse.age();
    item.owner = horse.owner();
    item.feedingSchedules = new ArrayList<>(horse.feedingSchedules().stream().map(FeedingItem::from).toList());
    return item;
  }

  public Horse toHorse() {
    return new Horse(horseId, name, breed, age, owner, toFeedingSchedules());
  }

  /** The feeding schedules in time order. */
  public List<FeedingSchedule> toFeedingSchedules() {
    if (feedingSchedules == null) {
      return List.of();
    }
    return feedingSchedules.stream().map(f -> f.toFeedingSchedule(horseId)).sorted(FEEDING_ORDER).toList();
  }

  public void addFeedingSchedule(FeedingSchedule schedule) {
    if (feedingSchedules == null) {
      feedingSchedules = new ArrayList<>();
    }
    feedingSchedules.add(FeedingItem.from(schedule));
  }

  /** Removes the feeding with that id; true if it was there. */
  public boolean removeFeedingSchedule(Long scheduleId) {
    return feedingSchedules != null && feedingSchedules.removeIf(f -> scheduleId.equals(f.getId()));
  }

  @DynamoDbPartitionKey
  public Long getHorseId() {
    return horseId;
  }

  public void setHorseId(Long horseId) {
    this.horseId = horseId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBreed() {
    return breed;
  }

  public void setBreed(String breed) {
    this.breed = breed;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public List<FeedingItem> getFeedingSchedules() {
    return feedingSchedules;
  }

  public void setFeedingSchedules(List<FeedingItem> feedingSchedules) {
    this.feedingSchedules = feedingSchedules;
  }

  @DynamoDbVersionAttribute
  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  /** One feeding, stored inside a horse item. */
  @DynamoDbBean
  public static class FeedingItem {

    private Long id;
    private LocalTime feedingTime;
    private String feedType;
    private BigDecimal quantity;
    private String unit;
    private String notes;

    public static FeedingItem from(FeedingSchedule schedule) {
      FeedingItem item = new FeedingItem();
      item.id = schedule.id();
      item.feedingTime = schedule.feedingTime();
      item.feedType = schedule.feedType();
      item.quantity = schedule.quantity();
      item.unit = schedule.unit();
      item.notes = schedule.notes();
      return item;
    }

    public FeedingSchedule toFeedingSchedule(Long horseId) {
      return new FeedingSchedule(id, horseId, feedingTime, feedType, quantity, unit, notes);
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public LocalTime getFeedingTime() {
      return feedingTime;
    }

    public void setFeedingTime(LocalTime feedingTime) {
      this.feedingTime = feedingTime;
    }

    public String getFeedType() {
      return feedType;
    }

    public void setFeedType(String feedType) {
      this.feedType = feedType;
    }

    public BigDecimal getQuantity() {
      return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
      this.quantity = quantity;
    }

    public String getUnit() {
      return unit;
    }

    public void setUnit(String unit) {
      this.unit = unit;
    }

    public String getNotes() {
      return notes;
    }

    public void setNotes(String notes) {
      this.notes = notes;
    }
  }
}
