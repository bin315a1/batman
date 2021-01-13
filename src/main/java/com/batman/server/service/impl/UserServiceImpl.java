package com.batman.server.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.batman.db.model.DBUser;
import com.batman.server.config.RuntimeProperties;
import com.batman.server.dao.impl.UserDaoImpl;
import com.batman.server.exception.*;
import com.batman.server.model.User;
import com.batman.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Value("${aws.sqs.batman-primary-sqs}")
    public String PRIMARY_QUEUE_URL;
    @Value("${aws.ses.sourceEmail}")
    public String SES_SOURCE_EMAIL;
    @Value("${aws.ses.destEmail}")
    public String SES_DEST_EMAIL;


    @Autowired
    UserDaoImpl userDao;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AmazonSimpleEmailService sesClient;
    @Autowired
    AmazonSQS sqsClient;
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

        // SES CONFIG
        // TODO: refactor this code into template
        // The subject line for the email.
        String subject = "Amazon SES test (AWS SDK for Java)";
        // The HTML body for the email.
        String htmlBody = "<h1>Amazon SES test (AWS SDK for Java)</h1>"
                +"<p>Click <a href='http://localhost:9090/validate-email/%s'> here </a>";
        String textBody = "This email was sent through Amazon SES "
                + "using the AWS SDK for Java.";

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(SES_DEST_EMAIL))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(String.format(htmlBody, authToken)))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(textBody)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(subject)))
                .withSource(SES_SOURCE_EMAIL);

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
    public void tester() {
        final SendMessageRequest message = new SendMessageRequest(PRIMARY_QUEUE_URL, "tester");
        message.setMessageGroupId("messageGroup1");
        message.setMessageDeduplicationId("1");
        final SendMessageResult res = sqsClient.sendMessage(message);
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
