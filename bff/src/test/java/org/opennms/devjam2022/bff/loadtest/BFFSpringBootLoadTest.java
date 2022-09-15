package org.opennms.devjam2022.bff.loadtest;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.devjam2022.bff.BFFApplication;
import org.slf4j.Logger;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@RunWith(SpringJUnit4ClassRunner.class)
public class BFFSpringBootLoadTest {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BFFSpringBootLoadTest.class);

    static GenericContainer<?> apiServer = new GenericContainer<>(DockerImageName.parse("openjdk:11"));

    final ExecutorService exService = Executors.newSingleThreadExecutor();

    // Suppose to simulate a Frontend, which is doing "simple" and plain "requests" to the BFF
    @Test
    public void testGetAll() throws InterruptedException {
        apiServer.withFileSystemBind(
                new File("../api-server/target/api-server-0.0.1-SNAPSHOT.jar").getAbsolutePath(),
                        "/app.jar")
                .withExposedPorts(8080)
                .waitingFor(Wait.forHttp("/v1/users").forStatusCode(200))
                .withCommand("java", "-jar", "/app.jar")
                .start();

        // Warming up?
        new TestRestTemplate().getForObject(
                "http://localhost:" + apiServer.getFirstMappedPort() + "/v1/users", String.class);
        new TestRestTemplate().getForObject(
                "http://localhost:" + apiServer.getFirstMappedPort() + "/v2/users", String.class);

        long start = System.currentTimeMillis();
        String response = new TestRestTemplate().getForObject(
                "http://localhost:" + apiServer.getFirstMappedPort() + "/v1/users", String.class);
        long firstRead = System.currentTimeMillis();
        LOG.info(">>>: Read '{}' characters", response.length());

        response = new TestRestTemplate().getForObject(
                "http://localhost:" + apiServer.getFirstMappedPort() + "/v2/users", String.class);
        long secondRead = System.currentTimeMillis();
        LOG.info(">>>: Read '{}' characters", response.length());

        LOG.info(">>>: First read took '{}' ms", firstRead - start);
        LOG.info(">>>: Second read took '{}' ms", secondRead - firstRead);

        // or use submit to get a Future (a result of computation, you'll need a Callable,
        // rather than runnable then)
        exService.execute(() -> {
            try {
                BFFApplication.main(new String[]{String.valueOf(apiServer.getFirstMappedPort())});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        exService.awaitTermination(3, TimeUnit.SECONDS);

        // Warming up actors?
        new TestRestTemplate().getForObject("http://localhost:8081/users", String.class);

        start = System.currentTimeMillis();
        response = new TestRestTemplate().getForObject("http://localhost:8081/users", String.class);
        firstRead = System.currentTimeMillis();

        LOG.info(">>>: Read '{}' characters", response.length());
        LOG.info(">>>: First read took '{}' ms", firstRead - start);

        exService.shutdownNow();
    }
}
