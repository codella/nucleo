package dk.codella.nucleo;

import io.vertx.ext.web.Router;

import java.util.function.Consumer;

public interface HttpRoutesProvider extends Consumer<Router> {

}
