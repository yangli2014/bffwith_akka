package org.opennms.devjam2022.apiserver.controller;

import java.util.List;
import org.opennms.devjam2022.apiserver.impl.IUserService;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Qualifier("InMemoryUserService")
    @Autowired
    IUserService userService;

    @GetMapping("/users")
    public List<UserWithRoles> all() {
        return userService.getUsers();
    }

}
