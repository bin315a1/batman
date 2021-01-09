package com.batman.server.model;

import com.batman.db.model.DBUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private DBUser.Status status;

    public boolean isCompleteObject() {
        if (this.id != null
                && this.firstName != null
                && this.lastName != null
                && this.email != null
                && this.status != null) {
            return true;
        }
        return false;
    }

//    public DBUser getDBUserFromUser(User user) {
//        DBUser dbUser = new DBUser();
//        dbUser.setId(user.getId());
//        dbUser.setFirstName(user.getFirstName());
//        dbUser.setLastName(user.getLastName());
//        dbUser.setEmail(user.getEmail());
//        dbUser.setStatus(user.getStatus());
//        return dbUser;
//    }
}
