package org.opennms.devjam2022.apiserver.controller;

import java.util.List;
import org.opennms.devjam2022.apiserver.impl.IUserService;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Qualifier("InMemoryUserService")
    @Autowired
    IUserService userService;

    @GetMapping("/")
    public Flux<UserWithRoles> all() {
        return Flux.fromIterable(userService.getUsers());
    }

    @GetMapping("/{userIdentity}/roles")
    public Flux<UserRole> allRoles(@PathVariable String userIdentity) {
        return Flux.fromIterable(userService.getRoles(userIdentity));
    }

    @PostMapping("/add")
    public Mono<String> addUser(@RequestBody UserWithRoles user) {
        return Mono.just(userService.addUser(user));
    }

    @PostMapping("/{userIdentity}/addRole")
    public Mono<String> addRole(@PathVariable String userIdentity, @RequestBody UserRole role) {
        return Mono.just(userService.addRole(userIdentity, role));
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
