package com.batman.server.dao;

import com.batman.db.model.DBUser;

import java.util.Optional;

public interface UserDao {
    public abstract DBUser getUser(String id);
    public abstract void createUser(DBUser user);
}