package com.coding2themax.petstore.data.model;

import java.util.Date;

public class Photo {

  private long id;
  private String fileName;
  private String fileType;
  private long fileSize;
  private String fileExtension;
  private String uploadPath;
  private Date uploadDate;
  private String photoUrls;

  public static class Builder {
    private long id;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String fileExtension;
    private String uploadPath;
    private Date uploadDate;
    private String photoUrls;

    public Builder id(long id) {
      this.id = id;
      return this;
    }

    public Builder fileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public Builder fileType(String fileType) {
      this.fileType = fileType;
      return this;
    }

    public Builder fileSize(long fileSize) {
      this.fileSize = fileSize;
      return this;
    }

    public Builder fileExtension(String fileExtension) {
      this.fileExtension = fileExtension;
      return this;
    }

    public Builder uploadPath(String uploadPath) {
      this.uploadPath = uploadPath;
      return this;
    }

    public Builder uploadDate(Date uploadDate) {
      this.uploadDate = uploadDate;
      return this;
    }

    public Builder photoUrls(String photoUrls) {
      this.photoUrls = photoUrls;
      return this;
    }

    public Photo build() {
      Photo photo = new Photo();
      photo.setId(id);
      photo.setFileName(fileName);
      photo.setFileType(fileType);
      photo.setFileSize(fileSize);
      photo.setFileExtension(fileExtension);
      photo.setUploadPath(uploadPath);
      photo.setUploadDate(uploadDate);
      photo.setPhotoUrls(photoUrls);
      return photo;
    }
  }

  public long getId() {
    return id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(final String fileType) {
    this.fileType = fileType;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(final long fileSize) {
    this.fileSize = fileSize;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public void setFileExtension(final String fileExtension) {
    this.fileExtension = fileExtension;
  }

  public String getUploadPath() {
    return uploadPath;
  }

  public void setUploadPath(final String uploadPath) {
    this.uploadPath = uploadPath;
  }

  public Date getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(final Date uploadDate) {
    this.uploadDate = uploadDate;
  }

  public String getPhotoUrls() {
    return photoUrls;
  }

  public void setPhotoUrls(final String photoUrls) {
    this.photoUrls = photoUrls;
  }

}
