package org.opennms.devjam2022.apiserver.service;

import java.util.Collections;
import java.util.List;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;

public interface IUserService {
    default List<UserWithRoles> getUsers() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    default UserWithRoles getUserByID(String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    default List<UserRole> getRoles(String userIdentity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    default String addUser(UserWithRoles user) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    default String addRole(String userIdentity, UserRole role) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    default boolean deleteRole(String userIdentity, String roleId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    default boolean deleteUser(String userIdentity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
