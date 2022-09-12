package org.opennms.devjam2022.apiserver.impl;

import java.util.List;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;

public interface IUserService {
    List<UserWithRoles> getUsers();
}
