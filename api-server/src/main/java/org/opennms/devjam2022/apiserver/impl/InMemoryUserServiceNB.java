package org.opennms.devjam2022.apiserver.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.opennms.devjam2022.apiserver.model.utils.ModelUtil;
import org.opennms.devjam2022.apiserver.service.AbstractInMemoryUserService;
import org.opennms.devjam2022.apiserver.service.IUserService;
import org.springframework.stereotype.Component;

/**
 * Simple in-memory implementation of the {@link IUserService} interface.
 */
@Component("InMemoryUserServiceNB")
public class InMemoryUserServiceNB extends AbstractInMemoryUserService {

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

    @Override
    public UserWithRoles getUserByID(String id) {
        UserWithRoles user = USERS.stream().filter(u -> u.getIdentity().equals(id)).findFirst().orElse(null);
        if(user != null) {
            user.setRoles(getRoles(id));
        }
        return user;
    }


    @Override
    public List<UserRole> getRoles(String userIdentity) {
        List<UserRole> result = USER_ROLES.get(userIdentity);
        return result == null ? Collections.emptyList() : result;
    }

    @Override
    public String addUser(UserWithRoles user) {
        user.setIdentity(ModelUtil.generateId());
        USERS.add(user);

        return user.getIdentity();
    }

    @Override
    public String addRole(String userIdentity, UserRole role) {
        role.setId(ModelUtil.generateId());

        if (!USER_ROLES.containsKey(userIdentity)) {
            USER_ROLES.put(userIdentity, new LinkedList<>());
        }

        USER_ROLES.get(userIdentity).add(role);
        return role.getId();
    }

    @Override
    public boolean deleteRole(String userIdentity, String roleId) {
        if (USER_ROLES.containsKey(userIdentity)) {
            List<UserRole> roles = USER_ROLES.get(userIdentity);
            return roles.removeIf(role -> role.getId().equals(roleId));
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteUser(String userIdentity) {
        boolean userWasRemoved = USERS.removeIf(user -> user.getIdentity().equals(userIdentity));
        USER_ROLES.remove(userIdentity);
        return userWasRemoved;
    }
}
