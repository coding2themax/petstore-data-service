package com.coding2themax.petstore.data.model;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Photo {

  private long id;
  private String fileName;
  private String fileType;
  private long fileSize;
  private String fileExtension;
  private String uploadPath;
  private Date uploadDate;
  private String photoUrls;

}
