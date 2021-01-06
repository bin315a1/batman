package com.batman.server.service.impl;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.batman.db.model.DBUser;
import com.batman.server.dao.impl.UserDaoImpl;
import com.batman.server.exception.BadRequestException;
import com.batman.server.exception.ResourceNotFoundException;
import com.batman.server.model.User;
import com.batman.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDaoImpl userDao;

    @Override
    public User getUser(String id) {
        Optional<DBUser> dbUserOptional = userDao.getUser(id);
        DBUser dbUser = dbUserOptional.orElseThrow(ResourceNotFoundException::new);

        User user = User.builder()
                .id(dbUser.getId())
                .email(dbUser.getEmail())
                .firstName(dbUser.getFirstName())
                .lastName(dbUser.getLastName())
                .build();

        return user;
    }

    @Override
    public void createUser(User user, String nonEncryptedPassword) {
        String encryptedPassword = nonEncryptedPassword;
        userDao.createUser(user, encryptedPassword);
    }

    @Override
    public void updateUser(String id, User user) {
        Optional<DBUser> dbUserOptional = userDao.getUser(id);
        DBUser originalDBUser = dbUserOptional.orElseThrow(BadRequestException::new);

        userDao.updateUser(getUpdatedDBUser(originalDBUser, user));
    }

    @Override
    public void deleteUser(String id) {
        Optional<DBUser> dbUserOptional = userDao.getUser(id);
        DBUser dbUser = dbUserOptional.orElseThrow(BadRequestException::new);

        userDao.deleteUser(dbUser);
    }

    private DBUser getUpdatedDBUser(DBUser dbUser, User user) {
        return DBUser.builder()
                .id(dbUser.getId())
                .firstName(user.getFirstName() != null ? user.getFirstName() : dbUser.getFirstName())
                .lastName(user.getLastName() != null ? user.getLastName() : dbUser.getLastName())
                .email(dbUser.getEmail())
                .password(dbUser.getPassword())
                .build();
    }
}
