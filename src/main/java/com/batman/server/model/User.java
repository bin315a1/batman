package com.batman.server.model;

import com.batman.db.model.DBUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
