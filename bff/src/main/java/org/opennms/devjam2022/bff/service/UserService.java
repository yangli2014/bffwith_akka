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
import akka.http.javadsl.model.HttpMethod;
import akka.http.javadsl.model.HttpMethods;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.RequestEntity;

public class UserService {
  private static final String BASE_SERVER_URL_USERS = "http://localhost:8080/v1/users";

  private final Http httpClient;

  public UserService(Http httpClient) {
    this.httpClient = httpClient;
  }

  public CompletionStage<HttpResponse> listUsers(){
    return executeRequest(BASE_SERVER_URL_USERS, HttpMethods.GET);
  }

  public CompletionStage<HttpResponse> getUserByID(long id) {
    return executeRequest(BASE_SERVER_URL_USERS + "/" + id, HttpMethods.GET);
  }

  public CompletionStage<HttpResponse> createUser(RequestEntity userData) {
    return executeRequest(BASE_SERVER_URL_USERS, HttpMethods.POST, userData);
  }

  public CompletionStage<HttpResponse> deleteUser(long id) {
    return executeRequest(BASE_SERVER_URL_USERS + "/" + id, HttpMethods.DELETE);
  }

  private CompletionStage<HttpResponse> executeRequest(String url, HttpMethod method) {
    return executeRequest(url, method, null);
  }

  private CompletionStage<HttpResponse> executeRequest(String url, HttpMethod method, RequestEntity data) {
    HttpRequest request = HttpRequest.create(url);
    request.withMethod(method);
    if(data != null) {
      request.withEntity(data);
    }
    return httpClient.singleRequest(request);
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
}
