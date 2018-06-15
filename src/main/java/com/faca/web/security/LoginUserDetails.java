package com.faca.web.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class LoginUserDetails extends User {

  private long no;

  public LoginUserDetails(com.faca.web.model.User user) {
    super(user.getProviderUserID(), user.getPassword(), AuthorityUtils.createAuthorityList("ROLE_USER"));
    no = user.getId();
  }

  public long getNo() {
    return no;
  }

}
