package com.faca.web.api;

import com.faca.web.model.CategoryDTO;
import com.faca.web.model.ResponseMessage;
import com.faca.web.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryAPI {

  @Autowired
  private CategoryService categoryService;

  @GetMapping()
  public ResponseEntity<ResponseMessage> getCategories(@RequestHeader(name = "token") String token) {
    return categoryService.getCategories(token);
  }

  @PostMapping()
  public ResponseEntity<ResponseMessage> addNewCategory(@RequestBody CategoryDTO category, @RequestHeader(name = "token") String token) {
    return categoryService.addNewCategory(category, token);
  }

  @DeleteMapping("/{categoryName}")
  public ResponseEntity<ResponseMessage> deleteCategory(@PathVariable String categoryName, @RequestHeader(name = "token") String token) {
    return categoryService.deleteCategory(categoryName, token);
  }

}
