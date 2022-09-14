package org.opennms.devjam2022.apiserver.controller;

import org.opennms.devjam2022.apiserver.impl.InMemoryUserServiceNB;
import org.opennms.devjam2022.apiserver.service.IUserService;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Second version (hence the "v2") implementation of the api-server for BFF tests
 * Will try to follow the "best practices" for a "reactive" spring-boot application
 * using non-blocking service of "flux" and "mono" types
 */
@RestController
@RequestMapping("/v2/users")
public class UserControllerNB {

    @Qualifier("InMemoryUserServiceNB")
    @Autowired
    InMemoryUserServiceNB userService;

    @GetMapping()
    public Flux<UserWithRoles> all() {
        return userService.getUsersReactively();
    }

    @GetMapping("/{id}")
    public Mono<UserWithRoles> getUserByID(@PathVariable String id) {
        // For the singe user, we can use the blocking version, there should be no performance gain
        return Mono.fromCallable(() -> userService.getUserByID(id));

    }

    @GetMapping("/{userIdentity}/roles")
    public Flux<UserRole> allRoles(@PathVariable String userIdentity) {
        return userService.getRolesReactively(userIdentity);
    }

    @PostMapping()
    public Mono<String> addUser(@RequestBody UserWithRoles user) {
        // For the singe user, we can use the blocking version, there should be no performance gain
        return Mono.fromCallable(() -> userService.addUser(user));
    }

    @PostMapping("/{userIdentity}/addRole")
    public Mono<String> addRole(@PathVariable String userIdentity, @RequestBody UserRole role) {
        // For the singe user, we can use the blocking version, there should be no performance gain
        return Mono.fromCallable(() -> userService.addRole(userIdentity, role));
    }

    @DeleteMapping("/{userIdentity}/deleteRole/{roleId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable String userIdentity, @PathVariable String roleId) {
        if (!userService.deleteRole(userIdentity, roleId)) {
            throw new IllegalArgumentException("Role  with provided id not found!");
        }
    }

    @DeleteMapping("/{userIdentity}/delete")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userIdentity) {
        if (!userService.deleteUser(userIdentity)) {
            throw new IllegalArgumentException("User with provided id not found!");
        }
    }
}
