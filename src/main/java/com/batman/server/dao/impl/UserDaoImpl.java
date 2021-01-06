package com.batman.server.dao.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.batman.db.model.DBUser;
import com.batman.server.dao.UserDao;
import com.batman.server.exception.BadRequestException;
import com.batman.server.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class UserDaoImpl implements UserDao {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public Optional<DBUser> getUser(String id) {
        Optional<DBUser> dbUserOptional = Optional.ofNullable(dynamoDBMapper.load(DBUser.class, id));

        return dbUserOptional;
    }

    @Override
    public void createUser(User newUser, String encryptedPassword) {
        DBUser dbUser;
        try {
             dbUser = DBUser.builder()
                    .id(newUser.getId())
                    .firstName(newUser.getFirstName())
                    .lastName(newUser.getLastName())
                    .email(newUser.getEmail())
                    .password(encryptedPassword)
                    .build();
        } catch (NullPointerException ne) {
            throw new BadRequestException();
        }

        dynamoDBMapper.save(dbUser);
    }

    @Override
    public void updateUser(DBUser updatedDBUser) {
        dynamoDBMapper.save(updatedDBUser);
    }

    @Override
    public void deleteUser(DBUser deleteUser) {
        dynamoDBMapper.delete(deleteUser);
    }
}
