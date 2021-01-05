package com.batman.server.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.batman.db.model.DBUser;
import com.batman.server.model.User;
import com.batman.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    DynamoDBMapper dynamoDBMapper;

    @Autowired
    AmazonDynamoDB amazonDynamoDB;

    @RequestMapping(value="/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getUser(@PathVariable("id") String id) {
//        CreateTableRequest tableRequest = dynamoDBMapper
//                .generateCreateTableRequest(DBUser.class);
//        tableRequest.setProvisionedThroughput(
//                new ProvisionedThroughput(1L, 1L));
//        amazonDynamoDB.createTable(tableRequest);

        User user = userService.getUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        userService.createUser(user);
        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateUser(@RequestBody User user) {
        userService.updateUser(user.getId(), user);
        return new ResponseEntity<>("User updated", HttpStatus.OK);
    }
}
