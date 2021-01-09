package com.batman.server.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.batman.db.model.DBUser;
import com.batman.server.config.RuntimeProperties;
import com.batman.server.dao.impl.UserDaoImpl;
import com.batman.server.exception.*;
import com.batman.server.model.User;
import com.batman.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    // The subject line for the email.
    static final String SUBJECT = "Amazon SES test (AWS SDK for Java)";

    // The HTML body for the email.
    static final String HTMLBODY = "<h1>Amazon SES test (AWS SDK for Java)</h1>"
            +"<p>Click <a href='http://localhost:9090/validate-email/%s'> here </a>";
//
//            + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
//            + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>"
//            + "AWS SDK for Java</a>"
//            + "";

    // The email body for recipients with non-HTML email clients.
    static final String TEXTBODY = "This email was sent through Amazon SES "
            + "using the AWS SDK for Java.";

    @Autowired
    UserDaoImpl userDao;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AmazonSimpleEmailService sesClient;
    @Autowired
    RuntimeProperties runtimeProperties;

    @Override
    public User getUser(String id) {
        Optional<DBUser> dbUserOptional = userDao.getUserById(id, true);
        DBUser dbUser = dbUserOptional.orElseThrow(HTTPResourceNotFoundException::new);

        User user = User.builder()
                .id(dbUser.getId())
                .email(dbUser.getEmail())
                .firstName(dbUser.getFirstName())
                .lastName(dbUser.getLastName())
                .build();

        return user;
    }

    @Override
    public void validateEmail(String email) {

        if (userDao.getUserByEmail(email, false).isPresent()) {
            throw new HTTPResourceConflictException();
        }

        userDao.createUser(email);

        String authToken = JWT.create()
                .withClaim("email", email)
                .sign(Algorithm.HMAC512(runtimeProperties.getJWTSecret()));

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses("bin315a1@gmail.com"))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(String.format(HTMLBODY, authToken)))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(SUBJECT)))
                .withSource("batman.devs@gmail.com");

        sesClient.sendEmail(request);
    }

    @Override
    public void validateUser(String email) {
        Optional<DBUser> dbUserOptional = userDao.getUserByEmail(email, false);
        DBUser dbUser = dbUserOptional.orElseThrow(HTTPResourceNotFoundException::new);

        if (dbUser.getStatus() != DBUser.Status.IN_VERIFICATION) {
            throw new HTTPMethodNotAllowed();
        }

        dbUser.setStatus(DBUser.Status.IN_REGISTRATION);
        userDao.updateUser(dbUser);
    }

    @Override
    public void createUser(User userUpdater, String nonEncryptedPassword) {
        Optional<DBUser> dbUserOptional = userDao.getUserByEmail(userUpdater.getEmail(), false);
        DBUser dbUserUpdater = dbUserOptional.orElseThrow(HTTPResourceNotFoundException::new);

        if (dbUserUpdater.getStatus() != DBUser.Status.IN_REGISTRATION) {
            throw new HTTPMethodNotAllowed();
        }

        String encryptedPassword = passwordEncoder.encode(nonEncryptedPassword);
        dbUserUpdater.setPassword(encryptedPassword);
        dbUserUpdater.setFirstName(userUpdater.getFirstName());
        dbUserUpdater.setLastName(userUpdater.getLastName());
        dbUserUpdater.setStatus(DBUser.Status.ACTIVE);

        userDao.updateUser(dbUserUpdater);
    }

    @Override
    public void updateUser(User user) {
        Optional<DBUser> dbUserOptional = userDao.getUserById(user.getId(), true);
        DBUser originalDBUser = dbUserOptional.orElseThrow(HTTPBadRequestException::new);

        userDao.updateUser(getUpdatedDBUser(originalDBUser, user));
    }

    @Override
    public void deleteUser(String id) {
        Optional<DBUser> dbUserOptional = userDao.getUserById(id, true);
        DBUser dbUser = dbUserOptional.orElseThrow(HTTPBadRequestException::new);

        userDao.deleteUser(dbUser);
    }

    @Override
    // misleading method name; it loads by email
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<DBUser> dbUserOptional = userDao.getUserByEmail(email, true);
        DBUser dbUser = dbUserOptional.orElseThrow(() -> new UsernameNotFoundException(email));

        return new org.springframework.security.core.userdetails.User(dbUser.getEmail(), dbUser.getPassword(), Collections.emptyList());
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
