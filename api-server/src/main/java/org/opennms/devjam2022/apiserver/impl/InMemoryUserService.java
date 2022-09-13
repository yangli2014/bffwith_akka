package org.opennms.devjam2022.apiserver.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.springframework.stereotype.Component;

/**
 * Simple in-memory implementation of the {@link IUserService} interface.
 */
@Component("InMemoryUserService")
public class InMemoryUserService implements IUserService {

    private static final HashMap USER_ROLES = new HashMap<String, List<UserRole>>();
    private static final List<UserWithRoles> USERS = new ArrayList<>();

    // TECH-DEBT: This is not thread-safe first of all. Secondly, we can't delete pre-created users or roles.
    // most probably need to be refactored later to something more sophisticated, considering that Akka may
    // use HTTP2.0 and streaming to access this?
    static {
        USER_ROLES.put("okta|08080dfjf90HF", List.of(
                new UserRole("9feda264-32d1-11ed-a261-0242ac120002", "ROLE1"),
                new UserRole("298b477c-3315-11ed-a261-0242ac120002", "ROLE2"),
                new UserRole("3a2606b2-3315-11ed-a261-0242ac120002", "ROLE")
        ));

        USER_ROLES.put("okta|08080dfjhghdf90HF", List.of(
                new UserRole("441328a8-3315-11ed-a261-0242ac120002", "ROLE2"),
                new UserRole("4c64ebcc-3315-11ed-a261-0242ac120002", "ROLE")
        ));

        USER_ROLES.put("okta|08080d798793fjf90HF", List.of(
                new UserRole("522469f2-3315-11ed-a261-0242ac120002", "ROLE1")
        ));

        USERS.addAll(List.of(
                new UserWithRoles(
                        "admin0831@opennms.com",
                        "okta|08080dfjf90HF",
                        "admin0831Name",
                        "admin0831LastName",
                        Collections.emptyList()
                ),
                new UserWithRoles(
                        "testuser@opennms.com",
                        "okta|08080dfjhghdf90HF",
                        "testuserName",
                        "testuserLastName",
                        Collections.emptyList()
                ),
                new UserWithRoles(
                        "interestinguser@opennms.com",
                        "okta|08080d798793fjf90HF",
                        "interestinguserName",
                        "interestinguserLastName",
                        Collections.emptyList()
                )));
    }

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
    public List<UserRole> getRoles(String userIdentity) {
        List<UserRole> result = (List<UserRole>) USER_ROLES.get(userIdentity);
        return result == null ? Collections.emptyList() : result;
    }

    @Override
    public String addUser(UserWithRoles user) {
        user.setIdentity(UUID.randomUUID().toString());
        USERS.add(user);

        return user.getIdentity();
    }

    @Override
    public String addRole(String userIdentity, UserRole role) {
        role.setId(UUID.randomUUID().toString());

        if (!USER_ROLES.containsKey(userIdentity)) {
            USER_ROLES.put(userIdentity, new LinkedList<>());
        }

        ((List<UserRole>) USER_ROLES.get(userIdentity)).add(role);
        return role.getId();
    }

    @Override
    public boolean deleteRole(String userIdentity, String roleId) {
        if (USER_ROLES.containsKey(userIdentity)) {
            List<UserRole> roles = (List<UserRole>) USER_ROLES.get(userIdentity);
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
