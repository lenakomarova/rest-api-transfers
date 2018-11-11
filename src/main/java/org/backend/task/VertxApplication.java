package org.backend.task;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.backend.task.web.RequestHandler;


public class VertxApplication extends AbstractVerticle {
//    private JDBCClient jdbc;
    private RequestHandler requestHandler = RequestHandler.getInstance();

    @Override
    public void start(Future<Void> fut) throws Exception {
//        jdbc = JDBCClient.createShared(vertx, config(), "DataSource");

//        startBackend(
//                (connection) -> createSomeData(connection,
//                        (nothing) ->
                                startWebApp(
                                (http) -> completeStartup(http, fut)
                        );
//                                        , fut
//                ), fut);
    }

//    private void startBackend(Handler<AsyncResult<SQLConnection>> next, Future<Void> fut) {
//        jdbc.getConnection(ar -> {
//            if (ar.failed()) {
//                fut.fail(ar.cause());
//            } else {
//                next.handle(Future.succeededFuture(ar.result()));
//            }
//        });
//    }

//    private void createSomeData(AsyncResult<SQLConnection> result, Handler<AsyncResult<Void>> next, Future<Void> fut) {
//        if (result.failed()) {
//            fut.fail(result.cause());
//        } else {
//            SQLConnection connection = result.result();
//
//            Scanner scanner = new Scanner(getClass().getResourceAsStream("db/schema.sql"), "UTF-8").useDelimiter(";");
//
//            while (scanner.hasNext()) {
//                connection.execute(scanner.next(),
//                        ar -> {
//                            if (ar.failed()) {
//                                fut.fail(ar.cause());
//                                connection.close();
//                            }
//                        });
//            }
//
//            connection.query("SELECT * FROM ACCOUNT_STATE_EVENT", select -> {
//                if (select.failed()) {
//                    fut.fail(select.cause());
//                    connection.close();
//                    return;
//                }
//                if (select.result().getNumRows() == 0) {
//                    Scanner insertScanner = new Scanner(getClass().getResourceAsStream("db/data.sql"), "UTF-8").useDelimiter(";");
//
//                    while (insertScanner.hasNext()) {
//                        connection.update(insertScanner.next(),
//                                (ar) -> {
//                                    if (ar.failed()) {
//                                        fut.fail(select.cause());
//                                        connection.close();
//                                    }
//                                });
//                    }
//                } else {
//                    next.handle(Future.<Void>succeededFuture());
//                    connection.close();
//                }
//            });
//    }
//}

    private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
        Router router = Router.router(vertx);

        router.get("/accounts").handler(requestHandler::getAll);
        router.route("/accounts*").handler(BodyHandler.create());
        router.post("/accounts").handler(requestHandler::create);
        router.get("/accounts/:id").handler(requestHandler::findById);
        router.delete("/accounts/:id").handler(requestHandler::close);

        router.get("/accounts/:id/transfers").handler(requestHandler::getTransfers);
        router.post("/accounts/:id/transfers/:to").handler(requestHandler::transfer);

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8080),
                        next::handle
                );
    }

    private void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut) {
        if (http.succeeded()) {
            fut.complete();
        } else {
            fut.fail(http.cause());
        }
    }

    @Override
    public void stop() throws Exception {
//        jdbc.close();
    }

}
