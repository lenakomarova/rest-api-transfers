package org.backend.task;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.backend.task.web.RequestHandler;

@Slf4j
public class VertxApplication extends AbstractVerticle {
    private final RequestHandler requestHandler = RequestHandler.INSTANCE;

    @Override
    public void start(Future<Void> fut) throws Exception {
        startWebApp(
                (http) -> completeStartup(http, fut));
    }

    private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
        Router router = Router.router(vertx);

        router.get("/accounts").handler(requestHandler::getAll);
        router.route("/accounts*").handler(BodyHandler.create());
        router.post("/accounts").handler(requestHandler::create);
        router.get("/accounts/:id").handler(requestHandler::findById);
        router.delete("/accounts/:id").handler(requestHandler::close);

        router.get("/accounts/:id/transfers").handler(requestHandler::getTransfers);
        router.post("/accounts/:id/transfers/:to").handler(requestHandler::transfer);

        log.info("starting netty on port: {}", config().getInteger("http.port"));
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8080),
                        next
                );
    }

    private void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut) {
        if (http.succeeded()) {
            fut.complete();
        } else {
            fut.fail(http.cause());
        }
    }

}
