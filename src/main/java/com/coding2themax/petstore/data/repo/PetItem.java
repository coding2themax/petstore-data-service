package com.coding2themax.petstore.data.repo;

import static com.coding2themax.petstore.data.config.DynamoDbProperties.PET_STATUS_INDEX;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openapitools.client.model.Category;
import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Tag;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

/**
 * A pet as stored in the DynamoDB pet table. Category and tags are stored inside the pet item;
 * {@code tagNames} repeats the tag names as a string set so pets can be filtered by tag.
 */
@DynamoDbBean
public class PetItem {

  private Long petId;
  private String name;
  private NamedItem category;
  private List<String> photoUrls;
  private List<NamedItem> tags;
  private Set<String> tagNames;
  private String status;

  public static PetItem from(Pet pet) {
    PetItem item = new PetItem();
    item.petId = pet.getId();
    item.name = pet.getName();
    if (pet.getCategory() != null) {
      item.category = NamedItem.of(pet.getCategory().getId(), pet.getCategory().getName());
    }
    item.photoUrls = pet.getPhotoUrls() == null || pet.getPhotoUrls().isEmpty() ? null : List.copyOf(pet.getPhotoUrls());
    if (pet.getTags() != null && !pet.getTags().isEmpty()) {
      item.tags = pet.getTags().stream().map(t -> NamedItem.of(t.getId(), t.getName())).toList();
      // DynamoDB does not allow empty sets, so leave it null when there are no tags
      item.tagNames = new LinkedHashSet<>(pet.getTags().stream().map(Tag::getName).toList());
    }
    item.status = pet.getStatus() == null ? null : pet.getStatus().getValue();
    return item;
  }

  public Pet toPet() {
    Pet pet = new Pet().id(petId).name(name);
    if (category != null) {
      pet.setCategory(new Category().id(category.getId()).name(category.getName()));
    }
    pet.setPhotoUrls(photoUrls == null ? List.of() : List.copyOf(photoUrls));
    pet.setTags(tags == null ? List.of() : tags.stream().map(t -> new Tag().id(t.getId()).name(t.getName())).toList());
    pet.setStatus(status == null ? null : Pet.StatusEnum.fromValue(status));
    return pet;
  }

  @DynamoDbPartitionKey
  public Long getPetId() {
    return petId;
  }

  public void setPetId(Long petId) {
    this.petId = petId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public NamedItem getCategory() {
    return category;
  }

  public void setCategory(NamedItem category) {
    this.category = category;
  }

  public List<String> getPhotoUrls() {
    return photoUrls;
  }

  public void setPhotoUrls(List<String> photoUrls) {
    this.photoUrls = photoUrls;
  }

  public List<NamedItem> getTags() {
    return tags;
  }

  public void setTags(List<NamedItem> tags) {
    this.tags = tags;
  }

  public Set<String> getTagNames() {
    return tagNames;
  }

  public void setTagNames(Set<String> tagNames) {
    this.tagNames = tagNames;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = PET_STATUS_INDEX)
  @DynamoDbAttribute("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  /** An id and name pair, used for a pet's category and tags. */
  @DynamoDbBean
  public static class NamedItem {

    private Long id;
    private String name;

    public static NamedItem of(Long id, String name) {
      NamedItem item = new NamedItem();
      item.id = id;
      item.name = name;
      return item;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
