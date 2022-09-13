package org.opennms.devjam2022.apiserver.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.opennms.devjam2022.apiserver.model.utils.ModelUtil;
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
    private static final String USER1_ID = ModelUtil.generateId();
    private static final String USER2_ID = ModelUtil.generateId();
    private static final String USER3_ID = ModelUtil.generateId();

    static {
        USER_ROLES.put(USER1_ID, List.of(
                new UserRole(ModelUtil.generateId(), "ROLE1"),
                new UserRole(ModelUtil.generateId(), "ROLE2"),
                new UserRole(ModelUtil.generateId(), "ROLE")
        ));

        USER_ROLES.put(USER2_ID, List.of(
                new UserRole(ModelUtil.generateId(), "ROLE2"),
                new UserRole(ModelUtil.generateId(), "ROLE")
        ));

        USER_ROLES.put(USER3_ID, List.of(
                new UserRole(ModelUtil.generateId(), "ROLE1")
        ));

        USERS.addAll(List.of(
                new UserWithRoles(
                        "admin0831@opennms.com",
                        USER1_ID,
                        "admin0831Name",
                        "admin0831LastName",
                        Collections.emptyList()
                ),
                new UserWithRoles(
                        "testuser@opennms.com",
                        USER2_ID,
                        "testuserName",
                        "testuserLastName",
                        Collections.emptyList()
                ),
                new UserWithRoles(
                        "interestinguser@opennms.com",
                        USER3_ID,
                        "interestinguserName",
                        "interestinguserLastName",
                        Collections.emptyList()
                )));
    }

    // TECH-DEBT: See the Issue #11 (https://github.com/yangli2014/bffwith_akka/issues/11)
    // we probably will need to modify it to the unblocking way.
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
        List<UserRole> result = (List<UserRole>) USER_ROLES.get(userIdentity);
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
