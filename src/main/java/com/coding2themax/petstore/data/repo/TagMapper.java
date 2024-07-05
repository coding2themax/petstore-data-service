package com.coding2themax.petstore.data.repo;

import java.util.List;
import java.util.Map;

import org.openapitools.client.model.Tag;

import io.r2dbc.spi.Row;
import reactor.core.publisher.Flux;

public class TagMapper {
  // generated code by r2dbc plugin
  public static Tag toTagfromRows(Row row) {
    Tag tag = new Tag();
    tag.setId(row.get("tagid", Long.class));
    tag.setName(row.get("tagname", String.class));
    return tag;
  }

  public static Tag toTagfromRows(Map<String, Object> row) {
    Tag tag = new Tag();
    tag.setId((Long) row.get("tagid"));
    tag.setName((String) row.get("tagname"));
    return tag;
  }

  public static Flux<Tag> toTagfromRows(List<Map<String, Object>> rows) {
    return Flux.fromIterable(rows).map(TagMapper::toTagfromRows);
  }
}
