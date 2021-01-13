package com.batman.server.service;

import com.batman.server.model.User;
import org.springframework.stereotype.Component;

public interface UserService {
    public abstract User getUser(String id);
    public abstract void validateEmail(String email);
    public abstract void validateUser(String email);
    public abstract void createUser(User user, String nonEncryptedPassword);
    public abstract void updateUser(User user);
    public abstract void tester();
}