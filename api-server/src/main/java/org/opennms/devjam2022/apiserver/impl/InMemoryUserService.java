package org.opennms.devjam2022.apiserver.impl;

import java.util.List;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.springframework.stereotype.Component;

/**
 * Simple in-memory implementation of the {@link IUserService} interface.
 */
@Component("InMemoryUserService")
public class InMemoryUserService implements IUserService {

    private final List<UserWithRoles> USERS = List.of(
            new UserWithRoles("admin0831@opennms.com", "okta|08080dfjf90HF", "admin0831Name", "admin0831LastName",
                    List.of(new UserRole("9feda264-32d1-11ed-a261-0242ac120002", "okta|08080dfjf90HF", "ROLE1"),
                            new UserRole("9feda264-32d1-11ed-a261-0242ac120002", "okta|08080dfjf90HF", "ROLE2"),
                            new UserRole("9feda264-32d1-11ed-a261-0242ac120002", "okta|08080dfjf90HF", "ROLE")))
    );

    @Override
    public List<UserWithRoles> getUsers() {
        return USERS;
    }
}
