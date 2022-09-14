package org.opennms.devjam2022.apiserver.controller;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;
import static org.opennms.devjam2022.apiserver.model.utils.ModelUtil.createTestRole;
import static org.opennms.devjam2022.apiserver.model.utils.ModelUtil.createTestUserWithEmptyListRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    public void testGetUsers() {
        List<UserWithRoles> users = userController.all();
        assertThat(users).isNotNull();
        assertThat(users).isNotEmpty();
    }

    @Test
    public void testAddUser() {
        UserWithRoles user = createTestUserWithEmptyListRoles();

        String userId = userController.addUser(user);
        assertThat(userId).isNotNull();

        List<UserWithRoles> users = userController.all();
        users.stream().filter(u -> u.getIdentity().equals(userId)).findFirst().ifPresentOrElse(u -> {
            assertThat(u.getIdentity()).isEqualTo(user.getIdentity());
            assertThat(u.getGivenName()).isEqualTo(user.getGivenName());
            assertThat(u.getFamilyName()).isEqualTo(user.getFamilyName());
            assertThat(u.getEmail()).isEqualTo(user.getEmail());
        }, () -> {
            throw new IllegalArgumentException("Added user not found!");
        });
    }

    @Test
    public void testAddRole() {
        UserWithRoles user = createTestUserWithEmptyListRoles();

        String userId = userController.addUser(user);
        assertThat(userId).isNotNull();

        UserRole role = createTestRole();
        String roleId = userController.addRole(userId, role);
        assertThat(roleId).isNotNull();

        // See that we can see the roles now
        List<UserRole> roles = userController.allRoles(userId);
        assertThat(roles).isNotNull();
        roles.stream().filter(r -> r.getId().equals(roleId)).findFirst().ifPresentOrElse(r -> {
            assertThat(r.getRole()).isEqualTo(role.getRole());
        }, () -> {
            throw new IllegalArgumentException("Added role not found!");
        });

        // And also the user should be there and have all the roles
        List<UserWithRoles> users = userController.all();
        assertThat(users).isNotNull();
        users.stream().filter(u -> u.getIdentity().equals(userId)).findFirst().ifPresentOrElse(u -> {
            assertThat(u.getIdentity()).isEqualTo(user.getIdentity());
            assertThat(u.getGivenName()).isEqualTo(user.getGivenName());
            assertThat(u.getFamilyName()).isEqualTo(user.getFamilyName());
            assertThat(u.getEmail()).isEqualTo(user.getEmail());
            assertThat(u.getRoles()).isNotNull();
            assertThat(u.getRoles()).isNotEmpty();
            u.getRoles().stream().filter(r -> r.getId().equals(roleId)).findFirst().ifPresentOrElse(r -> {
                assertThat(r.getRole()).isEqualTo(role.getRole());
            }, () -> {
                throw new IllegalArgumentException("Added role not found!");
            });
        }, () -> {
            throw new IllegalArgumentException("Added user not found!");
        });
    }

    @Test
    public void testDeleteUser() {
        UserWithRoles user = createTestUserWithEmptyListRoles();
        long userCount = userController.all().size();

        String userId = userController.addUser(user);
        assertThat(userId).isNotNull();

        assertThat(userController.all().size()).isEqualTo(userCount + 1);

        userController.deleteUser(userId);

        assertThat(userController.all().size()).isEqualTo(userCount);
    }

    @Test
    public void testDeleteRole() {
        UserWithRoles user = createTestUserWithEmptyListRoles();

        String userId = userController.addUser(user);
        assertThat(userId).isNotNull();

        assertThat(userController.all()).isNotEmpty();

        UserRole role = createTestRole();
        long roleCount = userController.allRoles(userId).size();
        String roleId = userController.addRole(userId, role);

        assertThat(userController.allRoles(userId).size()).isEqualTo(roleCount + 1);

        userController.deleteRole(userId, roleId);

        assertThat(userController.allRoles(userId).size()).isEqualTo(roleCount);
    }
}
