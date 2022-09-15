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
    final ExecutorService exService2 = Executors.newSingleThreadExecutor();

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

        BFFLoadTestData loadTestData = new BFFLoadTestData();
        // Warming up?
        new TestRestTemplate().getForObject(
                "http://localhost:" + apiServer.getFirstMappedPort() + "/v1/users", String.class);
        new TestRestTemplate().getForObject(
                "http://localhost:" + apiServer.getFirstMappedPort() + "/v2/users", String.class);

        long start = System.currentTimeMillis();
        String response = new TestRestTemplate().getForObject(
                "http://localhost:" + apiServer.getFirstMappedPort() + "/v1/users", String.class);
        long firstRead = System.currentTimeMillis();
        loadTestData.setDirectCallV1byteTransfer(response.getBytes().length);

        response = new TestRestTemplate().getForObject(
                "http://localhost:" + apiServer.getFirstMappedPort() + "/v2/users", String.class);
        long secondRead = System.currentTimeMillis();
        loadTestData.setDirectCallV2byteTransfer(response.getBytes().length);

        // storing reads into the loadTestData
        loadTestData.setDirectCallV1(firstRead - start);
        loadTestData.setDirectCallV2(secondRead - firstRead);

        // or use submit to get a Future (a result of computation, you'll need a Callable,
        // rather than runnable then)
        exService.execute(() -> {
            try {
                BFFApplication.main(new String[]{String.valueOf(apiServer.getFirstMappedPort()), "1"});
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

        exService.shutdownNow();

        loadTestData.setBffCallV1byteTransfer(response.getBytes().length);
        loadTestData.setBffCallV1(firstRead - start);

        // NOTE: we do need second executor here, since the first one usually not in time to shutdown and release the port
        exService2.execute(() -> {
            try {
                BFFApplication.main(new String[]{String.valueOf(apiServer.getFirstMappedPort()), "2", "8082"});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        exService2.awaitTermination(3, TimeUnit.SECONDS);

        // Warming up actors?
        new TestRestTemplate().getForObject("http://localhost:8082/users", String.class);

        start = System.currentTimeMillis();
        response = new TestRestTemplate().getForObject("http://localhost:8082/users", String.class);
        firstRead = System.currentTimeMillis();

        exService2.shutdownNow();

        loadTestData.setBffCallV2byteTransfer(response.getBytes().length);
        loadTestData.setBffCallV2(firstRead - start);

        LOG.info("===================================");
        LOG.info("Direct call V1: {}ms", loadTestData.getDirectCallV1());
        LOG.info("Direct call V1 transfer: {}bytes", loadTestData.getDirectCallV1byteTransfer());
        LOG.info("Direct call V2: {}ms", loadTestData.getDirectCallV2());
        LOG.info("Direct call V2 transfer: {}bytes", loadTestData.getDirectCallV2byteTransfer());
        LOG.info("===================================");
        LOG.info("BFF call V1: {}ms", loadTestData.getBffCallV1());
        LOG.info("BFF call V1 transfer: {}bytes", loadTestData.getBffCallV1byteTransfer());
        LOG.info("BFF call V2: {}ms", loadTestData.getBffCallV2());
        LOG.info("BFF call V2 transfer: {}bytes", loadTestData.getBffCallV2byteTransfer());
        LOG.info("===================================");
    }
}
