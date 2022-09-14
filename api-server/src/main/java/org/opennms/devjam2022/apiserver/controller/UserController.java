package org.opennms.devjam2022.apiserver.controller;

import java.util.List;
import org.opennms.devjam2022.apiserver.service.IUserService;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * This is the first (hence the "v1") implementation of the api-server for BFF tests
 * this one will NOT be using the "reactive" spring-boot application but instead returns
 * "usual" responses imitating "simple" api-server, unadapted to the "reactive" world
 */
@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Qualifier("InMemoryUserService")
    @Autowired
    IUserService userService;

    @GetMapping()
    public List<UserWithRoles> all() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserWithRoles getUserByID(@PathVariable String id) {
        return userService.getUserByID(id);

    }

    @GetMapping("/{userIdentity}/roles")
    public List<UserRole> allRoles(@PathVariable String userIdentity) {
        return userService.getRoles(userIdentity);
    }

    @PostMapping()
    public String addUser(@RequestBody UserWithRoles user) {
        return userService.addUser(user);
    }

    @PostMapping("/{userIdentity}/addRole")
    public String addRole(@PathVariable String userIdentity, @RequestBody UserRole role) {
        return userService.addRole(userIdentity, role);
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
