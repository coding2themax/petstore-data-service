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

  public Photo() {
  }

  public Photo(long id, String fileName, String fileType, long fileSize, String fileExtension,
      String uploadPath, Date uploadDate, String photoUrls) {
    this.id = id;
    this.fileName = fileName;
    this.fileType = fileType;
    this.fileSize = fileSize;
    this.fileExtension = fileExtension;
    this.uploadPath = uploadPath;
    this.uploadDate = uploadDate;
    this.photoUrls = photoUrls;
  }

  private Photo(Builder builder) {
    this(builder.id, builder.fileName, builder.fileType, builder.fileSize, builder.fileExtension,
        builder.uploadPath, builder.uploadDate, builder.photoUrls);
  }

  public static Builder builder() {
    return new Builder();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public void setFileExtension(String fileExtension) {
    this.fileExtension = fileExtension;
  }

  public String getUploadPath() {
    return uploadPath;
  }

  public void setUploadPath(String uploadPath) {
    this.uploadPath = uploadPath;
  }

  public Date getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(Date uploadDate) {
    this.uploadDate = uploadDate;
  }

  public String getPhotoUrls() {
    return photoUrls;
  }

  public void setPhotoUrls(String photoUrls) {
    this.photoUrls = photoUrls;
  }

  public static final class Builder {

    private long id;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String fileExtension;
    private String uploadPath;
    private Date uploadDate;
    private String photoUrls;

    private Builder() {
    }

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
      return new Photo(this);
    }
  }
}
