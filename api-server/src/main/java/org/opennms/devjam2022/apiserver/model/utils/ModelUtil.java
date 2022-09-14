package org.opennms.devjam2022.apiserver.model.utils;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.opennms.devjam2022.apiserver.model.UserRole;
import org.opennms.devjam2022.apiserver.model.UserWithRoles;

public class ModelUtil {
    private static final Random ID_GENERATOR = new Random();

    public static String generateId() {
        // NOTE: This is a little bit hacky, but it works for us as unsigned LONG example
        return Long.toUnsignedString(ID_GENERATOR.nextLong());
    }

    public static UserRole createTestRole() {
        String id = generateId();
        return new UserRole(id, "TestRole" + id);
    }

    public static UserWithRoles createTestUserWithEmptyListRoles() {
        return createTestUserWithRoles(Collections.emptyList());
    }

    public static UserWithRoles createTestUserWithRoles(List<UserRole> roles) {
        String id = generateId();
        return new UserWithRoles(
                "tst" + id + "@opennms.com",
                id,
                "TestName" + id,
                "TestFamily" + id,
                roles);
    }
}
