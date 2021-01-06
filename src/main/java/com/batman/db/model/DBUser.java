package com.batman.db.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import lombok.*;

@DynamoDBTable(tableName = "dbuser")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DBUser {
    public static final String TABLE_NAME = "dbuser";

    // db field names
    public static final String ID = "id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    // POJO fields
    @DynamoDBHashKey(attributeName = ID)
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBAttribute(attributeName = FIRST_NAME)
    @NonNull
    private String firstName;

    @DynamoDBAttribute(attributeName = LAST_NAME)
    @NonNull
    private String lastName;

    @DynamoDBAttribute(attributeName = EMAIL)
    @NonNull
    private String email;

    @DynamoDBAttribute(attributeName = PASSWORD)
    @NonNull
    private String password;

    public static GetItemSpec get(String id) {
        return new GetItemSpec().withPrimaryKey("id", id);
    }
}
