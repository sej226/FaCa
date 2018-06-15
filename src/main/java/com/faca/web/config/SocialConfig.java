package com.faca.web.config;

import com.faca.web.security.FacebookSignInAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.SignInAdapter;

@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {

  @Override
  public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
    InMemoryUsersConnectionRepository repository = new InMemoryUsersConnectionRepository(connectionFactoryLocator);
    repository.setConnectionSignUp(new QuickConnectionSignup());
    return repository;
  }

  private static class QuickConnectionSignup implements ConnectionSignUp {
    @Override
    public String execute(Connection<?> connection) {
      return connection.getKey().getProviderUserId();
    }
  }

  @Bean
  public SignInAdapter signInAdapter() {
    return new FacebookSignInAdapter(new HttpSessionRequestCache());
  }
}
