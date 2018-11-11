package org.backend.task;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.backend.task.dto.Account;
import org.backend.task.dto.Transfer;
import org.backend.task.service.impl.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

@RunWith(VertxUnitRunner.class)
public class VertxTest extends AbstractTest {
    private Vertx vertx;
    private Integer port;
    private Account unlimitedAccount;
    private Account myAccount;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();

        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.port", port)
                );

        vertx.deployVerticle(VertxApplication.class.getName(), options, context.asyncAssertSuccess());
        unlimitedAccount = fillAccount(BigDecimal.valueOf(Long.MAX_VALUE));
        myAccount = fillAccount(BigDecimal.TEN);
    }


    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void getAccounts(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/accounts", response -> {
            response.handler(body -> {
                context.assertTrue(response.headers().get("content-type").contains("application/json"));
                context.assertEquals(response.statusCode(), 200);
                final List<Account> accounts = Arrays.asList(Json.decodeValue(body.toString(), Account[].class));
                context.assertEquals(2, accounts.size());
                async.complete();
            });
        });
    }

    @Test
    public void transfer(TestContext context) {
        Async async = context.async();
        final String json = Json.encodePrettily(Transfer.builder()
                .amount(BigDecimal.TEN)
                .description("web test")
                .involvedAccount(myAccount.getId())
                .build());
        vertx.createHttpClient().post(port, "localhost", "/accounts/" + unlimitedAccount.getId() + "/transfers/" + myAccount.getId())
                .putHeader("content-type", "application/json")
                .putHeader("content-length", Integer.toString(json.length()))
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    async.complete();
                })
                .write(json)
                .end();
    }


}
