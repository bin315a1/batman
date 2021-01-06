package com.batman.server.dao;

import com.batman.db.model.DBUser;
import com.batman.server.model.User;

import java.util.Optional;

public interface UserDao {
    public abstract Optional<DBUser> getUser(String id);
    public abstract void createUser(User user, String encryptedPassword);
    public abstract void updateUser(DBUser user);
    public abstract void deleteUser(DBUser user);
}