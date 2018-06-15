package com.faca.web.util;

import com.faca.web.model.User;
import com.faca.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SessionUser {

  @Autowired
  private UserRepository userRepository;

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userName = authentication.getName();
    return userRepository.findByProviderUserID(userName);
  }

  public User getUserByToken(String token) {
    return userRepository.findByToken(token);
  }
}
