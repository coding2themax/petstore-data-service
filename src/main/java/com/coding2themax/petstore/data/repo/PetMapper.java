package com.coding2themax.petstore.data.repo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.openapitools.client.model.Category;
import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Tag;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PetMapper {

  public static Mono<Pet> toPetfromRows(List<Map<String, Object>> rows) {
    Pet pet = new Pet();
    pet = toPet(rows.get(0));
    List<String> photoUrls = rows.stream().map(r -> {
      return toPhotoUrls(r);
    }).distinct().collect(Collectors.toList());
    pet.setPhotoUrls(photoUrls);

    List<Tag> tags = rows.stream().map(r -> {
      return toTag(r);
    }).distinct().collect(Collectors.toList());
    pet.setTags(tags);
    pet.setStatus(Pet.StatusEnum.fromValue((String) rows.get(0).get("petstatus")));
    return Mono.just(pet);
  }

  public static Pet toPet(Map<String, Object> petMap) {
    // Implement the logic to convert the map to a Pet object
    // ...

    Category category = new Category();

    BigDecimal bigDecimalCategory = (BigDecimal) petMap.get("petid");
    category.setId(bigDecimalCategory.longValue());
    category.setName((String) petMap.get("category_name"));

    Tag tag = new Tag();
    BigDecimal bigDecimalCategorytagid = (BigDecimal) petMap.get("tagid");
    tag.setId(bigDecimalCategorytagid.longValue());

    tag.setName((String) petMap.get("tagname"));

    BigDecimal bd = (BigDecimal) petMap.get("petid");
    Pet pet = new Pet();
    pet.setId(bd.longValue());
    pet.setName((String) petMap.get("name"));
    pet.setCategory(category);
    // pet.setTags(List.of(tag));
    // pet.setStatus(Pet.StatusEnum.valueOf((String) petMap.get("petstatus")));
    return pet;

  }

  private static Category toCategory(Map<String, Object> categoryMap) {
    // Implement the logic to convert the map to a Category object
    // ...
    return null;
  }

  private static Tag toTag(Map<String, Object> tagMap) {
    // Implement the logic to convert the map to a Tag object
    // ...
    if (tagMap.get("tagid") != null) {
      BigDecimal bigDecimalCategory = (BigDecimal) tagMap.get("tagid");

      return new Tag().id(bigDecimalCategory.longValue()).name((String) tagMap.get("tagname"));
    }
    return null;
  }

  private static Pet.StatusEnum toStatusEnum(Map<String, Object> statusEnumMap) {
    // Implement the logic to convert the map to a StatusEnum object
    // ...
    return null;
  }

  private static String toPhotoUrls(Map<String, Object> photoUrlsMap) {
    // Implement the logic to convert the map to a List<String> object
    // ...
    if (photoUrlsMap.get("photourl") != null) {
      return (String) photoUrlsMap.get("photourl");
    }
    return null;
  }
}
