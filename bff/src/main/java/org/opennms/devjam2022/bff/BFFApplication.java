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

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.ContentType;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.MediaType;
import akka.http.javadsl.model.MediaTypes;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

public class BFFApplication extends AllDirectives {
  public static void main(String[] args) throws IOException {
    ActorSystem<Void> actorSystem = ActorSystem.create(Behaviors.empty(), "bff-actor-system");

    final Http http = Http.get(actorSystem);

    BFFApplication app = new BFFApplication();

    final CompletionStage<ServerBinding> binding = http.newServerAt("localhost", 8080)
        .bind(app.createRoute());

    System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
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

  private Route createRoute() {
    return concat(
        get(() ->
            path("users", () -> {
              CompletionStage<Optional<String>> result = listUsers();
              return onSuccess(result, maybe -> maybe.map(str -> complete(StatusCodes.OK, str))
                  .orElseGet(() -> complete(StatusCodes.NOT_FOUND, "{\"error\": \"Not Found\"}")));
            }))
        ,
        get(() ->
            pathPrefix("users", () ->
                path(longSegment(), (Long id) -> {
                  final CompletionStage<Optional<String>> futureMaybeUser = getUserById(id);
                  return onSuccess(futureMaybeUser, maybeUser ->
                      maybeUser.map(user -> complete(StatusCodes.OK, user))
                          .orElseGet(() -> complete(StatusCodes.NOT_FOUND, "{\"error\": \"Not Found\"}"))
                  );
                }))),
        post(() ->
            path("users", () -> extractRequestEntity(entity -> {
              final HttpEntity.Strict strict = (HttpEntity.Strict) entity;
              System.out.println(strict.getData().utf8String());
              return complete(StatusCodes.OK, "{\"data\": \"data saved\"}");
            })))
    );
  }
}
