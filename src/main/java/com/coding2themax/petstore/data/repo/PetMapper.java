package com.coding2themax.petstore.data.repo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openapitools.client.model.Category;
import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Tag;

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
    return pet;

  }

  public static Tag toTag(Map<String, Object> tagMap) {
    BigDecimal bigDecimalCategory = (BigDecimal) tagMap.get("tagid");
    return new Tag().id(bigDecimalCategory.longValue()).name((String) tagMap.get("tagname"));
  }

  public static String toPhotoUrls(Map<String, Object> photoUrlsMap) {
    return (String) photoUrlsMap.get("photourl");
  }
}
