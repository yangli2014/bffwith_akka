package org.opennms.devjam2022.apiserver.listener;

import java.util.Random;
import org.opennms.devjam2022.apiserver.impl.InMemoryUserServiceNB;
import static org.opennms.devjam2022.apiserver.model.utils.ModelUtil.createTestRole;
import static org.opennms.devjam2022.apiserver.model.utils.ModelUtil.createTestUserWithEmptyListRoles;
import org.opennms.devjam2022.apiserver.service.IUserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ContextListener {
    private final static Logger LOG = org.slf4j.LoggerFactory.getLogger(ContextListener.class);

    public static final int NUMBER_OF_USERS_IN_DB = 1000;

    @Qualifier("InMemoryUserService")
    @Autowired
    IUserService userService;

    @Qualifier("InMemoryUserServiceNB")
    @Autowired
    InMemoryUserServiceNB userServiceNb;

    @EventListener({ContextRefreshedEvent.class})
    public void handleContextRefreshEvent() {
        LOG.info("Context refresh event received");
        for (long i = 0; i < NUMBER_OF_USERS_IN_DB; i++) {
            String userId = userService.addUser(createTestUserWithEmptyListRoles());
            String userIdNb = userServiceNb.addUser(createTestUserWithEmptyListRoles());

            int count = new Random().nextInt(5) + 1; // 1-5 roles to be added
            for (int j = 0; j < count; j++) {
                userService.addRole(userId, createTestRole());
                userServiceNb.addRole(userIdNb, createTestRole());
            }
        }
        LOG.info("Added '" + NUMBER_OF_USERS_IN_DB + "' users... to the in-memory database");
    }
}
