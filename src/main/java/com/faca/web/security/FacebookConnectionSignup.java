package com.faca.web.security;

import com.faca.web.model.User;
import com.faca.web.repository.UserRepository;
import com.faca.web.util.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String execute(Connection<?> connection) {
        System.out.println("signup === ");
        final User user = new User();

        if (isExistsUser(connection.getKey().getProviderUserId())) {
            return connection.getDisplayName();
        }

        user.setProviderUserID(connection.getKey().getProviderUserId());
        user.setUsername(connection.getDisplayName());
        user.setPassword(randomAlphabetic(8));
        user.setToken(SHA256.generateSHA256(connection.getKey().getProviderUserId()));
        userRepository.save(user);
        return user.getUsername();
    }

    private boolean isExistsUser(String providerUserID) {
        User u = userRepository.findByProviderUserID(providerUserID);
        if (u == null)
            return false;
        return true;
    }

}