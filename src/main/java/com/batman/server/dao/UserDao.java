package com.batman.server.dao;

import com.batman.db.model.DBUser;
import com.batman.server.model.User;

import java.util.Optional;

public interface UserDao {
    public abstract Optional<DBUser> getUserById(String id, boolean activeOnly);
    public abstract Optional<DBUser> getUserByEmail(String email, boolean activeOnly);
    public abstract void createUser(String email);
    public abstract void updateUser(DBUser user);
    public abstract void deleteUser(DBUser user);
}