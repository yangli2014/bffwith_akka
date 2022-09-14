/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.devjam2022.bff.spring.service;


import org.opennms.devjam2022.bff.spring.model.UserWithRoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class GatewayService {
  private final WebClient webClient;

  public GatewayService(String baseURL) {
    webClient = WebClient.builder()
        .baseUrl(baseURL)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public Flux<UserWithRoles> listUsers() {
    return webClient.method(HttpMethod.GET).uri("/users")
        .retrieve().bodyToFlux(UserWithRoles.class);
  }

  public Mono<UserWithRoles> getUserByID(String id){
    return executeRequest("/users/" + id, HttpMethod.GET, UserWithRoles.class);
  }

  public Mono<String> createUser(String data) {
    return executeRequest("/users", HttpMethod.POST, data, String.class);
  }

  public Mono<Void> deleteUser(String id) {
    return executeRequest("/users/" + id, HttpMethod.DELETE, Void.TYPE);
  }

  private <T> Mono <T> executeRequest(String path, HttpMethod method, Class<T> returnType) {
    return executeRequest(path, method, null, returnType);
  }

  private <T> Mono <T> executeRequest(String path, HttpMethod method, String data, Class<T> returnType) {
    WebClient.RequestBodySpec request = webClient
        .method(method)
        .uri(path);
    if(data != null) {
      request.contentType(MediaType.APPLICATION_JSON).bodyValue(data);
    }
    WebClient.ResponseSpec response =  request.retrieve();
    return response.bodyToMono(returnType);
  }
}
