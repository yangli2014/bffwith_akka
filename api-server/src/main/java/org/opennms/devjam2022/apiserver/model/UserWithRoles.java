package org.opennms.devjam2022.apiserver.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserWithRoles {
    String email;
    String identity;
    String givenName;
    String familyName;

    List<UserRole> roles;
}
