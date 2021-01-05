package com.batman.db.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Objects;

@DynamoDBTable(tableName = "users")
public class DBUser {
    @DynamoDBHashKey
    private String id;
    @DynamoDBAttribute
    private String name;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if(!(o instanceof DBUser)) {
            return false;
        }

        DBUser forum = (DBUser) o;
        if (!Objects.equals(name, forum.name)) {
            return false;
        }
        if (!Objects.equals(id, forum.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int res = id!=null ? name.hashCode() : 0;
        res = 31 * res + (name!=null ? name.hashCode() : 0);
        return res;
    }
}
