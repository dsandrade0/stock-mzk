package br.com.teste.stock_mzk.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.validation.ValidationException;

public class FalhaHandler {


  public void erro(RoutingContext ctx) {
    var falha = ctx.failure();
    if (falha instanceof ValidationException) {
      var mensagemErro = falha.getMessage();
      var jo = new JsonObject();
      jo.put("erro", mensagemErro);
      ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(jo.toString());
    } else {
      ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
    }
  }
}
