package com.faca.web.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {

  private String name;

  public Category toEntity() {
    Category category = new Category();
    category.setName(this.name);
    return category;
  }

}
