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

package org.opennms.devjam2022.bff;

import static akka.http.javadsl.server.PathMatchers.longSegment;
import static akka.http.javadsl.server.PathMatchers.segment;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.opennms.devjam2022.bff.service.UserService;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

public class BFFApplication extends AllDirectives {
  private final UserService service;

  private BFFApplication(UserService service) {
    this.service = service;
  }

  public static void main(String[] args) throws IOException {
    ActorSystem<Void> actorSystem = ActorSystem.create(Behaviors.empty(), "bff-actor-system");

    final Http http = Http.get(actorSystem);

    UserService userService = new UserService(http);

    BFFApplication app = new BFFApplication(userService);

    final CompletionStage<ServerBinding> binding = http.newServerAt("localhost", 8081)
        .bind(app.createRoute());

    System.out.println("Server online at http://localhost:8081/\nPress RETURN to stop...");
    System.in.read();

    binding.thenCompose(ServerBinding::unbind)
        .thenAccept(unbound -> actorSystem.terminate());

  }

  private CompletionStage<Optional<String>> listUsers() {
    return CompletableFuture.completedFuture(Optional.of("{\"data\": \"list of users\"}"));
  }

  private CompletionStage<Optional<String>> getUserById(long id) {
    if(id > 0 && id < 10) {
      return CompletableFuture.completedFuture(Optional.of("{\"data\": \"user " + id +"\"}"));
    } else {
      return CompletableFuture.completedFuture(Optional.empty());
    }
  }

  private void logResponse(HttpResponse response) {
    System.out.println(response.status() + ": " + response.entity().toString());
  }

  private Route createRoute() {
    return concat(
        get(() ->
            path("users", () -> {
              CompletionStage<HttpResponse> responseFuture = service.listUsers();
              return onSuccess(responseFuture, response -> complete(response.status(), response.entity()));
            }))
        ,
        get(() ->
            pathPrefix("users", () ->
                path(segment(), (String id) -> {
                  final CompletionStage<HttpResponse> responseFuture = service.getUserByID(id);
                  return onSuccess(responseFuture, response -> complete(response.status(), response.entity()));
                }))),
        post(() ->
            path("users", () -> extractRequestEntity(entity -> {
              CompletionStage<HttpResponse> responseFuture = service.createUser(entity);
              return onSuccess(responseFuture, res -> complete(res.status(), res.entity()));
            }))),
        delete(() -> pathPrefix("users", () ->
            path(longSegment(), (Long id) -> {
             CompletionStage<HttpResponse> responseFuture = service.deleteUser(id);
             return onSuccess(responseFuture, res -> complete(res.status(), res.entity()));
            })))
    );
  }
}
