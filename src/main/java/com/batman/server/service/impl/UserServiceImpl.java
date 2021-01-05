package com.batman.server.service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.batman.db.model.DBUser;
import com.batman.server.dao.impl.UserDaoImpl;
import com.batman.server.exception.UserNotFoundException;
import com.batman.server.model.User;
import com.batman.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDaoImpl userDao;

    @Autowired
    AmazonDynamoDB amazonDynamoDB;

    private static Map<String, User> userDB = new HashMap<>();

    @Override
    public User getUser(String id) {
//        if(!userDB.containsKey(id)) throw new UserNotFoundException();

        ////
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
//        Table table = dynamoDB.getTable("dbuser");
        String tableName = "Movies";

        try {
            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("year", KeyType.HASH), // Partition
                            // key
                            new KeySchemaElement("title", KeyType.RANGE)), // Sort key

                    Arrays.asList(new AttributeDefinition("year", ScalarAttributeType.N),
                            new AttributeDefinition("title", ScalarAttributeType.S)),

                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
        ////

//        DBUser dbUser = userDao.getUser(id);
        User user = new User();
//        user.setName(dbUser.getName());
//        user.setId(dbUser.getId());

        return user;
    }

    @Override
    public void createUser(User user) {
        DBUser dbUser = new DBUser();
        dbUser.setId(user.getId());
        dbUser.setName(user.getName());
        userDao.createUser(dbUser);
    }

    @Override
    public void updateUser(String id, User user) {
        if(!userDB.containsKey(id)) throw new UserNotFoundException();
        userDB.get(id).setName(user.getName());
    }

    @Override
    public void deleteUser(String id) {
        userDB.remove(id);
    }


}
