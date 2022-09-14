package org.opennms.devjam2022.apiserver.impl;

import java.util.*;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.opennms.devjam2022.apiserver.service.AbstractInMemoryUserService;
import org.opennms.devjam2022.apiserver.service.IUserService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * In memory implementation of the {@link IUserService} interface. Tries to use the idea of
 * "reactive" spring-boot to make the implementation more efficient. Basically there are only two methods
 * which make sense to be non-blocking: {@link #getUsersReactively()} and {@link #getRolesReactively(String)}
 * The other ones can be just made from the "usual" calls
 */
@Component("InMemoryUserServiceNB")
public class InMemoryUserServiceNB extends AbstractInMemoryUserService {

    public Flux<UserWithRoles> getUsersReactively() {
        // Let's parallelize the work with long streams here and see how it works out
        return Flux.fromStream(USERS.stream().parallel().map(user -> {
            final List<UserRole> roles = getRoles(user.getIdentity());
            return new UserWithRoles(
                    user.getEmail(),
                    user.getIdentity(),
                    user.getGivenName(),
                    user.getFamilyName(),
                    roles);
        }));
    }

    public Flux<UserRole> getRolesReactively(String userIdentity) {
        List<UserRole> result = USER_ROLES.get(userIdentity);
        return result == null ? Flux.empty() : Flux.fromStream(result.stream().parallel());
    }
}
