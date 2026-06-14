package com.modi.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.modi.api.entity.User;
import com.modi.api.repo.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(User user) {

        if(userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        userRepository.save(user);

        return "Registration Successful";
    }

public User login(User user) {

    User dbUser =
            userRepository.findByEmail(
                    user.getEmail()
            ).orElseThrow(
                    () -> new RuntimeException("User not found")
            );

    System.out.println("Entered Password : " + user.getPassword());
    System.out.println("DB Password      : " + dbUser.getPassword());

    boolean match = passwordEncoder.matches(
            user.getPassword(),
            dbUser.getPassword()
    );

    System.out.println("Password Match : " + match);

    if (!match) {
        throw new RuntimeException("Invalid Password");
    }

    return dbUser ;
}
}
