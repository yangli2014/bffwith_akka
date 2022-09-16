package org.opennms.devjam2022.apiserver.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.opennms.devjam2022.apiserver.model.utils.ModelUtil;

/**
 * Abstract implementation of the {@link IUserService} that uses an in-memory map to store the users
 * and their roles
 */
public abstract class AbstractInMemoryUserService implements IUserService {
    protected final ConcurrentHashMap<String, List<UserRole>> USER_ROLES = new ConcurrentHashMap<>();
    protected final List<UserWithRoles> USERS = new ArrayList<>();

    // ======== These methods are common to the both (blocking and non-blocking) implementations ========
    @Override
    public List<UserRole> getRoles(String userIdentity) {
        List<UserRole> result = USER_ROLES.get(userIdentity);
        return result == null ? Collections.emptyList() : result;
    }

    @Override
    public synchronized UserWithRoles getUserByID(String id) {
        UserWithRoles user = USERS.stream().filter(u -> u.getIdentity().equals(id)).findFirst().orElse(null);
        if(user != null) {
            user.setRoles(getRoles(id));
        }
        return user;
    }

    public synchronized String addUser(UserWithRoles user) {
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
    public synchronized boolean deleteUser(String userIdentity) {
        boolean userWasRemoved = USERS.removeIf(user -> user.getIdentity().equals(userIdentity));
        USER_ROLES.remove(userIdentity);
        return userWasRemoved;
    }
}
