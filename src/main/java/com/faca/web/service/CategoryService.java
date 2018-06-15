package com.faca.web.service;

import com.faca.web.model.Category;
import com.faca.web.model.CategoryDTO;
import com.faca.web.model.ResponseMessage;
import com.faca.web.model.User;
import com.faca.web.repository.CategoryRepository;
import com.faca.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

import static com.faca.web.model.MessageStatus.*;

@Service
public class CategoryService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Transactional
  public ResponseEntity<ResponseMessage> addNewCategory(CategoryDTO categoryVO, String token) {
    User user = userRepository.findByToken(token);
    ResponseMessage message;

    if (user == null) {
      message = new ResponseMessage(false, INVALID_TOKEN);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    Category category = categoryVO.toEntity();
    categoryRepository.save(category);
    user.addCategory(category);
    userRepository.save(user);

    message = new ResponseMessage(true, SUCCESS_ADD_CATEGORY);
    return new ResponseEntity<ResponseMessage>(message, HttpStatus.CREATED);
  }

  public ResponseEntity<ResponseMessage> getCategories(String token) {
    User user = userRepository.findByToken(token);
    ResponseMessage message;

    if (user == null) {
      message = new ResponseMessage(false, INVALID_TOKEN);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    message = new ResponseMessage(true, user.getCategories());
    return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
  }

  public ResponseEntity<ResponseMessage> deleteCategory(String categoryName, String token) {
    User user = userRepository.findByToken(token);
    ResponseMessage message;
    boolean existsCategory = false;

    if (user == null) {
      message = new ResponseMessage(false, INVALID_TOKEN);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    List<Category> categoryList = user.getCategories();
    for (Category category : categoryList) {
      if (category.equals(categoryName)) {
        existsCategory = true;
        break;
      }
    }

    if (existsCategory == false) {
      message = new ResponseMessage(false, NOT_FOUND_CATEGORY);
      return new ResponseEntity<ResponseMessage>(message, HttpStatus.NOT_FOUND);
    }

    user.deleteCategory(categoryName);
    userRepository.save(user);
    message = new ResponseMessage(true, SUCCESS_DELETE_CATEGORY);
    return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);
  }

}
