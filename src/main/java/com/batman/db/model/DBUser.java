package com.batman.db.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import lombok.*;

import java.lang.annotation.ElementType;

@DynamoDBTable(tableName = "dbuser")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DBUser {
    public static final String TABLE_NAME = "dbuser";
    public static final String GSI_EMAIL_INDEX_NAME = "Email-index";

    public enum Status {
        ACTIVE("active"),
        IN_VERIFICATION("inVerification"),
        IN_REGISTRATION("inRegistration"),
        INACTIVE("inactive");

        private final String status;
        private Status(String status) {
            this.status = status;
        }
    }

    // db field names
    public static final String ID = "id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String STATUS = "accountStatus";    // "status" was a DynamoDB reserved keyword

    // POJO fields
    @DynamoDBHashKey(attributeName = ID)
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBIndexHashKey(attributeName = EMAIL, globalSecondaryIndexName = GSI_EMAIL_INDEX_NAME)
    @NonNull
    private String email;

    @DynamoDBAttribute(attributeName = FIRST_NAME)
    private String firstName;

    @DynamoDBAttribute(attributeName = LAST_NAME)
    private String lastName;

    @DynamoDBAttribute(attributeName = PASSWORD)
    private String password;

    @DynamoDBAttribute(attributeName = STATUS)
    @DynamoDBTypeConvertedEnum
    @NonNull
    private Status status;
}
