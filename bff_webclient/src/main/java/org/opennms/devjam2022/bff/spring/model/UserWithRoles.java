package org.opennms.devjam2022.bff.spring.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithRoles {
    String email;
    String identity;
    String givenName;
    String familyName;

    List<UserRole> roles;
}
