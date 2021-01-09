package com.batman.server.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.batman.server.config.SecurityConfig;
import com.batman.server.exception.HTTPUnauthorizedException;
import com.batman.server.model.User;
import com.batman.server.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    Environment env;

    @RequestMapping(value="/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getUser(@PathVariable("id") String id) {

        User user = userService.getUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        userService.createUser(user, "password");
        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateUser(@RequestBody User userUpdater) {
        userService.updateUser(userUpdater);
        return new ResponseEntity<>("User updated", HttpStatus.OK);
    }

    @RequestMapping(value = "/validate-email", method = RequestMethod.POST)
    public ResponseEntity<Object> initiateEmailValidation(@RequestBody String email) {
        userService.validateEmail(email);
        return new ResponseEntity<>("Email Sent", HttpStatus.OK);
    }

    @RequestMapping(value = "/validate-email/{authToken}", method = RequestMethod.GET)
    public ResponseEntity<Object> resolveEmailValidation(@PathVariable("authToken") String authToken) {
        DecodedJWT decodedToken = JWT.decode(authToken);
        String email = (String)decodedToken.getClaim("email").asString();
        userService.validateUser(email);
        return new ResponseEntity<>("Email Validated", HttpStatus.OK);
    }

    @RequestMapping(value = "/users/signup", method = RequestMethod.PATCH)
    public ResponseEntity<Object> userSignup(@RequestHeader(name = "Authorization") String authToken, @RequestBody SignupRequestWrapper signupRequestWrapper) {
        DecodedJWT decodedToken = JWT.decode(SecurityConfig.getJWTToken(authToken));
        String tokenEmail = (String)decodedToken.getClaim("email").asString();
        if (!tokenEmail.equals(signupRequestWrapper.getUserUpdater().getEmail())) {
            throw new HTTPUnauthorizedException();
        }

        userService.createUser(signupRequestWrapper.getUserUpdater(), signupRequestWrapper.getPassword());
        return new ResponseEntity<>("User Created", HttpStatus.OK);
    }

    // Request Wrappers
    @Data
    public static class SignupRequestWrapper {
        String password;
        User userUpdater;
    }
}
