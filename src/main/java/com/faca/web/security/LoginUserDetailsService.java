package com.faca.web.security;

import com.faca.web.model.User;
import com.faca.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginUserDetailsService implements UserDetailsService {

  @Autowired
  private UserService userService;

  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
    User user = userService.getUser(id);

    if (user == null) {
      System.out.println("loadUserByUsername : not existed user");
      throw new UsernameNotFoundException("login fail");
    }

    return new LoginUserDetails(user);
  }
}
