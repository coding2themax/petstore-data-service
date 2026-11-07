package com.coding2themax.petstore.data.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.openapitools.client.model.Category;
import org.openapitools.client.model.Pet;
import org.openapitools.client.model.Tag;

class PetItemTest {

  @Test
  void roundTripsAFullPet() {
    Pet pet = new Pet().id(1L).name("Angelfish").category(new Category().id(2L).name("Fish"))
        .photoUrls(List.of("a.gif", "b.gif"))
        .tags(List.of(new Tag().id(1L).name("tag1"), new Tag().id(2L).name("tag2")))
        .status(Pet.StatusEnum.PENDING);

    PetItem item = PetItem.from(pet);

    assertEquals(Set.of("tag1", "tag2"), item.getTagNames());
    assertEquals("pending", item.getStatus());
    assertEquals(pet, item.toPet());
  }

  @Test
  void leavesEmptyCollectionsUnsetBecauseDynamoDbRejectsEmptySets() {
    PetItem item = PetItem.from(new Pet().id(1L).name("Plain").photoUrls(List.of()).tags(List.of()));

    assertNull(item.getTagNames());
    assertNull(item.getTags());
    assertNull(item.getPhotoUrls());
    assertNull(item.getCategory());
    assertNull(item.getStatus());
  }

  @Test
  void readsAnItemWithMissingAttributes() {
    PetItem item = new PetItem();
    item.setPetId(5L);
    item.setName("Bare");

    Pet pet = item.toPet();

    assertEquals(5L, pet.getId());
    assertNull(pet.getCategory());
    assertEquals(List.of(), pet.getPhotoUrls());
    assertEquals(List.of(), pet.getTags());
    assertNull(pet.getStatus());
  }

  @Test
  void handlesNullTagAndPhotoLists() {
    Pet pet = new Pet().id(1L).name("Nulls");
    pet.setTags(null);
    pet.setPhotoUrls(null);

    PetItem item = PetItem.from(pet);

    assertNull(item.getTags());
    assertNull(item.getPhotoUrls());
  }

  @Test
  void settersAndGettersMatch() {
    PetItem item = new PetItem();
    PetItem.NamedItem category = PetItem.NamedItem.of(1L, "Fish");
    item.setCategory(category);
    item.setPhotoUrls(List.of("x"));
    item.setTags(List.of(category));
    item.setTagNames(Set.of("Fish"));
    item.setStatus("sold");

    assertEquals(category, item.getCategory());
    assertEquals(List.of("x"), item.getPhotoUrls());
    assertEquals(List.of(category), item.getTags());
    assertEquals(Set.of("Fish"), item.getTagNames());
    assertEquals("sold", item.getStatus());

    category.setId(9L);
    category.setName("Birds");
    assertEquals(9L, category.getId());
    assertEquals("Birds", category.getName());
  }
}
