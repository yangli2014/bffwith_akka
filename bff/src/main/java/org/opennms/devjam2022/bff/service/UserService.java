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

package org.opennms.devjam2022.bff.service;

import java.util.concurrent.CompletionStage;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.RequestEntity;

public class UserService {

  public static final int DEFAULT_PORT = 8081;
  private static final String BASE_SERVER_URL_USERS_P1 = "http://localhost:";
  private static final String BASE_SERVER_URL_USERS_P2 = "/v1/users";

  private final Http httpClient;
  private final int port;

  public UserService(Http httpClient, int port) {
    this.httpClient = httpClient;
    this.port = port;
  }

  public CompletionStage<HttpResponse> listUsers(){
    return get(getBaseServerUrlUsers());
  }

  public CompletionStage<HttpResponse> getUserByID(String id) {
    return get(getBaseServerUrlUsers() + "/" + id);
  }

  public CompletionStage<HttpResponse> createUser(RequestEntity userData) {
    return post(getBaseServerUrlUsers(), userData);
  }

  public CompletionStage<HttpResponse> deleteUser(long id) {
    return delete(getBaseServerUrlUsers() + "/" + id + "/delete");
  }

  private CompletionStage<HttpResponse> get(String url) {
    return httpClient.singleRequest(HttpRequest.GET(url));
  }

  private CompletionStage<HttpResponse> post(String url, RequestEntity data) {
    return httpClient.singleRequest(HttpRequest.POST(url).withEntity(data));
  }

  private CompletionStage<HttpResponse> delete(String url) {
    return httpClient.singleRequest(HttpRequest.DELETE(url));
  }

  private String getBaseServerUrlUsers() {
    return BASE_SERVER_URL_USERS_P1 + port + BASE_SERVER_URL_USERS_P2;
  }
}
