package com.faca.web.repository;

import com.faca.web.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
    User findByUsername(String username);

    User findByProviderUserID(String providerUserId);

    User findByToken(String token);
}
