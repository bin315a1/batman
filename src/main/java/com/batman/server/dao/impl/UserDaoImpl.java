package com.batman.server.dao.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.batman.db.model.DBUser;
import com.batman.server.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class UserDaoImpl implements UserDao {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public Optional<DBUser> getUserById(String id, boolean activeOnly) {
        DBUser dbUser = dynamoDBMapper.load(DBUser.class, id);
        dbUser = getActiveUserOnly(dbUser, activeOnly);
        Optional<DBUser> dbUserOptional = Optional.ofNullable(dbUser);

        return dbUserOptional;
    }

    @Override
    public Optional<DBUser> getUserByEmail(String email, boolean activeOnly) {
        Map<String, AttributeValue> vals = new HashMap<>();
        vals.put(":v_email", new AttributeValue().withS(email));

        DynamoDBQueryExpression<DBUser> queryExp = new DynamoDBQueryExpression<DBUser>()
                .withKeyConditionExpression("email = :v_email")
                .withIndexName(DBUser.GSI_EMAIL_INDEX_NAME)
                .withExpressionAttributeValues(vals)
                .withConsistentRead(false);

        List queryRes = dynamoDBMapper.query(DBUser.class, queryExp);
        DBUser dbUser = queryRes.size() < 1 ? null : (DBUser)queryRes.get(0);
        dbUser = getActiveUserOnly(dbUser, activeOnly);
        Optional<DBUser> dbUserOptional = Optional.ofNullable(dbUser);

        return dbUserOptional;
    }

    @Override
    public void createUser(String email) {
        DBUser dbUser;

        dbUser = DBUser.builder()
                .email(email)
                .status(DBUser.Status.IN_VERIFICATION)
                .build();

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

    private DBUser getActiveUserOnly(DBUser dbUser, boolean activeOnly) {
        if (activeOnly && dbUser.getStatus() != DBUser.Status.ACTIVE) {
            return null;
        }
        return dbUser;
    }
}
