package org.opennms.devjam2022.apiserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;

/**
 * Abstract implementation of the {@link IUserService} that uses an in-memory map to store the users
 * and their roles
 */
public abstract class AbstractInMemoryUserService implements IUserService {
    protected final ConcurrentHashMap<String, List<UserRole>> USER_ROLES = new ConcurrentHashMap<>();
    protected final List<UserWithRoles> USERS = new ArrayList<>();

}
