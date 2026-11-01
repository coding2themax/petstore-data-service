package com.coding2themax.petstore.data.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

class PhotoTest {

  private final Date uploadDate = new Date(0);

  @Test
  void builderSetsAllFields() {
    Photo photo = Photo.builder()
        .id(1L)
        .fileName("dog")
        .fileType("image/png")
        .fileSize(1024L)
        .fileExtension("png")
        .uploadPath("/uploads")
        .uploadDate(uploadDate)
        .photoUrls("http://example.com/dog.png")
        .build();

    assertFields(photo);
  }

  @Test
  void settersSetAllFields() {
    Photo photo = new Photo();
    assertNull(photo.getFileName());

    photo.setId(1L);
    photo.setFileName("dog");
    photo.setFileType("image/png");
    photo.setFileSize(1024L);
    photo.setFileExtension("png");
    photo.setUploadPath("/uploads");
    photo.setUploadDate(uploadDate);
    photo.setPhotoUrls("http://example.com/dog.png");

    assertFields(photo);
  }

  private void assertFields(Photo photo) {
    assertEquals(1L, photo.getId());
    assertEquals("dog", photo.getFileName());
    assertEquals("image/png", photo.getFileType());
    assertEquals(1024L, photo.getFileSize());
    assertEquals("png", photo.getFileExtension());
    assertEquals("/uploads", photo.getUploadPath());
    assertEquals(uploadDate, photo.getUploadDate());
    assertEquals("http://example.com/dog.png", photo.getPhotoUrls());
  }
}
