package org.opennms.devjam2022.apiserver.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.opennms.devjam2022.apiserver.service.AbstractInMemoryUserService;
import org.opennms.devjam2022.apiserver.service.IUserService;
import org.springframework.stereotype.Component;

/**
 * Simple in-memory implementation of the {@link IUserService} interface.
 */
@Component("InMemoryUserService")
public class InMemoryUserService extends AbstractInMemoryUserService {

    @Override
    public List<UserWithRoles> getUsers() {
        return USERS.stream().map(user -> {
            final List<UserRole> roles = getRoles(user.getIdentity());
            return new UserWithRoles(
                    user.getEmail(),
                    user.getIdentity(),
                    user.getGivenName(),
                    user.getFamilyName(),
                    roles);
        }).collect(Collectors.toList());
    }

}
