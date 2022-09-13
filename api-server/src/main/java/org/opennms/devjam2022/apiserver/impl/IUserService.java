package org.opennms.devjam2022.apiserver.impl;

import java.util.List;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;

public interface IUserService {
    List<UserWithRoles> getUsers();

    List<UserRole> getRoles(String userIdentity);

    String addUser(UserWithRoles user);

    String addRole(String userIdentity, UserRole role);

    boolean deleteRole(String userIdentity, String roleId);

    boolean deleteUser(String userIdentity);
}
