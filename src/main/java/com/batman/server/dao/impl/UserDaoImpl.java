package com.batman.server.dao.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.batman.db.model.DBUser;
import com.batman.server.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDaoImpl implements UserDao {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public DBUser getUser(String id) {
        return dynamoDBMapper.load(DBUser.class, id);
    }

    @Override
    public void createUser(DBUser user) {
        dynamoDBMapper.save(user);
    }
}
