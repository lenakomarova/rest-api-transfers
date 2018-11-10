package org.backend.task.web;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.task.dto.Account;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;
import org.backend.task.service.AccountService;

import java.util.List;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestHandler {
    private static RequestHandler INSTANCE = new RequestHandler();
    public static RequestHandler getInstance() {
        return INSTANCE;
    }

    private AccountService accountService = AccountService.getInstance();

    public void getAll(RoutingContext routingContext) {
        List<Account> accounts = accountService.findAll();
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(accounts));
    }

    public void create(RoutingContext routingContext) {
        Optional<Account> account = accountService.create();
        if (account.isPresent()) {
            routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .setStatusCode(201)
                    .end(Json.encodePrettily(account.get()));
        } else {
            routingContext.response()
                    .setStatusCode(500)
                    .end();
        }
    }

    public void findById(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(417).end();
        } else {
            Optional<Account> account = accountService.findById(Long.valueOf(id));
            if (account.isPresent()) {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(account.get()));
            } else {
                routingContext.response()
                        .setStatusCode(404)
                        .end();
            }
        }
    }

    public void close(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(417).end();
        } else {
            Optional<Account> account = accountService.close(Long.valueOf(id));
            if (account.isPresent()) {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(204)
                        .end(Json.encodePrettily(account.get()));
            } else {
                routingContext.response()
                        .setStatusCode(404)
                        .end();
            }
        }
    }

    public void getTransfers(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(417).end();
        } else {
            List<Transfer> transfers = accountService.getTransfers(Long.valueOf(id));
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(204)
                        .end(Json.encodePrettily(transfers));
        }
    }

    public void transfer(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        final Transfer transfer = Json.decodeValue(routingContext.getBodyAsString(), Transfer.class);
        if (id == null || transfer == null) {
            routingContext.response().setStatusCode(417).end();
        } else {
            Optional<TransferError> error = accountService.transfer(Long.valueOf(id), transfer);
            if (error.isPresent()) {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(417)
                        .end(Json.encodePrettily(error.get()));
            } else {
                routingContext.response()
                        .setStatusCode(200)
                        .end();
            }
        }
    }
}
