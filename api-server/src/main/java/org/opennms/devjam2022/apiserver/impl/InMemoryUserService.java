package org.opennms.devjam2022.apiserver.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import static org.opennms.devjam2022.apiserver.model.utils.ModelUtil.ID_GENERATOR;
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
    private static final Long USER1_ID = ID_GENERATOR.nextLong();
    private static final Long USER2_ID = ID_GENERATOR.nextLong();
    private static final Long USER3_ID = ID_GENERATOR.nextLong();

    static {
        USER_ROLES.put(USER1_ID, List.of(
                new UserRole(ID_GENERATOR.nextLong(), "ROLE1"),
                new UserRole(ID_GENERATOR.nextLong(), "ROLE2"),
                new UserRole(ID_GENERATOR.nextLong(), "ROLE")
        ));

        USER_ROLES.put(USER2_ID, List.of(
                new UserRole(ID_GENERATOR.nextLong(), "ROLE2"),
                new UserRole(ID_GENERATOR.nextLong(), "ROLE")
        ));

        USER_ROLES.put(USER3_ID, List.of(
                new UserRole(ID_GENERATOR.nextLong(), "ROLE1")
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
            final List<UserRole> roles = getRoles(user.getIdentity().toString());
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
        List<UserRole> result = (List<UserRole>) USER_ROLES.get(Long.parseLong(userIdentity));
        return result == null ? Collections.emptyList() : result;
    }

    @Override
    public String addUser(UserWithRoles user) {
        user.setIdentity(ID_GENERATOR.nextLong());
        USERS.add(user);

        return user.getIdentity().toString();
    }

    @Override
    public String addRole(String userIdentity, UserRole role) {
        role.setId(ID_GENERATOR.nextLong());
        Long userId = Long.parseLong(userIdentity);

        if (!USER_ROLES.containsKey(userId)) {
            USER_ROLES.put(userId, new LinkedList<>());
        }

        ((List<UserRole>) USER_ROLES.get(userId)).add(role);
        return role.getId().toString();
    }

    @Override
    public boolean deleteRole(String userIdentity, String roleId) {
        Long userId = Long.parseLong(userIdentity);
        Long rlId = Long.parseLong(roleId);

        if (USER_ROLES.containsKey(userId)) {
            List<UserRole> roles = (List<UserRole>) USER_ROLES.get(userId);
            return roles.removeIf(role -> role.getId().equals(rlId));
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteUser(String userIdentity) {
        Long userId = Long.parseLong(userIdentity);

        boolean userWasRemoved = USERS.removeIf(user -> user.getIdentity().equals(userId));
        USER_ROLES.remove(userId);
        return userWasRemoved;
    }
}
