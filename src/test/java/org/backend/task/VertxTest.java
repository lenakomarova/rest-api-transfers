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
import org.backend.task.dto.TransferError;
import org.backend.task.service.impl.AbstractTest;
import org.backend.task.service.impl.ServiceContext;
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
        unlimitedAccount = fillAccount(BigDecimal.valueOf(Long.MAX_VALUE), ServiceContext.INSTANCE);
        myAccount = fillAccount(BigDecimal.TEN, ServiceContext.INSTANCE);
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
                context.assertTrue(accounts.contains(myAccount));
                context.assertTrue(accounts.contains(unlimitedAccount));
                async.complete();
            });
        });
    }

    @Test
    public void createAccount(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().post(port, "localhost", "/accounts", response -> {
            response.handler(body -> {
                context.assertTrue(response.headers().get("content-type").contains("application/json"));
                context.assertEquals(response.statusCode(), 201);
                final Account account = Json.decodeValue(body.toString(), Account.class);
                context.assertNotNull(account);
                async.complete();
            });
        })
                .end();
    }

    @Test
    public void getAccount(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/accounts/" + myAccount.getId(), response -> {
            response.handler(body -> {
                context.assertTrue(response.headers().get("content-type").contains("application/json"));
                context.assertEquals(response.statusCode(), 200);
                final Account account = Json.decodeValue(body.toString(), Account.class);
                context.assertNotNull(account);
                async.complete();
            });
        });
    }

    @Test
    public void getInvalidAccount(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/accounts/" + 100500, response -> {
            context.assertEquals(response.statusCode(), 404);
            async.complete();
        });
    }

    @Test
    public void closeAccount(TestContext context) {
        final Async async = context.async();
        Account toClose = fillAccount(BigDecimal.ZERO, ServiceContext.INSTANCE);

        vertx.createHttpClient().delete(port, "localhost", "/accounts/" + toClose.getId(), response -> {
            context.assertEquals(response.statusCode(), 204);
            async.complete();
        })
                .end();
    }

    @Test
    public void closeInvalidAccount(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().delete(port, "localhost", "/accounts/" + 100500, response -> {
            context.assertEquals(response.statusCode(), 404);
            async.complete();
        })
                .end();
    }

    @Test
    public void getTransfers(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/accounts/" + myAccount.getId() + "/transfers", response -> {
            response.handler(body -> {
                context.assertTrue(response.headers().get("content-type").contains("application/json"));
                List<Transfer> transfers = Arrays.asList(Json.decodeValue(body.toString(), Transfer[].class));
                context.assertEquals(response.statusCode(), 200);
                context.assertNotNull(transfers);
                context.assertEquals(1, transfers.size());
                async.complete();
            });
        });
    }

    @Test
    public void getTransfersByInvalidAccount(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/accounts/" + "asv" + "/transfers", response -> {
            context.assertEquals(response.statusCode(), 417);
            async.complete();
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

    @Test
    public void insufficientFundsTransfer(TestContext context) {
        Async async = context.async();
        final String json = Json.encodePrettily(Transfer.builder()
                .amount(BigDecimal.valueOf(11))
                .description("insufficient funds")
                .involvedAccount(0)
                .build());
        vertx.createHttpClient().post(port, "localhost", "/accounts/" + myAccount.getId() + "/transfers/" + unlimitedAccount.getId())
                .putHeader("content-type", "application/json")
                .putHeader("content-length", Integer.toString(json.length()))
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 417);
                    response.handler(body -> {
                        context.assertTrue(body.toString().contains(TransferError.INSUFFICIENT_FUNDS.getText()));
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }

    @Test
    public void failStartup(TestContext context) throws IOException {
        vertx = Vertx.vertx();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.port", -1)
                );

        vertx.deployVerticle(VertxApplication.class.getName(), options, context.asyncAssertFailure());
    }


}
