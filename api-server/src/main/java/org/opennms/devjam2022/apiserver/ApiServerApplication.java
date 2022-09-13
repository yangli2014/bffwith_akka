package org.opennms.devjam2022.apiserver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "InMemory API Server", version = "1.0", description = "User and Roles CRUD API"))
public class ApiServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiServerApplication.class, args);
  }

}
