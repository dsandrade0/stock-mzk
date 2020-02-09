package br.com.teste.stock_mzk.handler;

import br.com.teste.stock_mzk.model.Produto;
import br.com.teste.stock_mzk.service.DbServico;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.RequestParameters;

public class ProdutoHandler {
  private DbServico dbServico = DbServico.getInstance();

  public void getTodosProdutosEmEstoque(RoutingContext ctx) {
    try {
      var array = new JsonArray(dbServico.listarProdutosEmEstoque());
      ctx.response().setStatusCode(200).end(array.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void salvarProduto(RoutingContext ctx) {
    try {
      var params = (RequestParameters) ctx.get("parsedParameters");
      if (params != null) {
        var obj = params.body().getJsonObject();
        var produto = obj.mapTo(Produto.class);
        if (produto.validar()) {
          if (!dbServico.produtoJaCadastrado(produto)) {
            dbServico.insere(produto);
            ctx.response().setStatusCode(HttpResponseStatus.CREATED.code()).end(Json.encode(produto));
          } else {
            ctx.response().setStatusCode(HttpResponseStatus.FOUND.code()).end();
          }
        } else {
          ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
        }
      }
    } catch (DecodeException e) {
      e.printStackTrace();
      ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end(e.getMessage());
    }
  }

  public void deleteProduto(RoutingContext ctx) {
    var obj = ctx.getBodyAsJson();
    var numeroSerie = obj.getString("numeroSerie");
    var codigoBarras = obj.getString("codigoBarras");
    var produto = new Produto(codigoBarras, numeroSerie);
    if (dbServico.produtoJaCadastrado(produto)) {
      dbServico.remove(produto);
      ctx.response().setStatusCode(HttpResponseStatus.OK.code()).end();
    } else {
      ctx.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end();
    }
  }

  public void venderProduto(RoutingContext ctx) {
    try {
      var params = (RequestParameters) ctx.get("parsedParameters");
      if (params != null) {
        var objetoJson = params.body().getJsonObject();
        var produto = objetoJson.mapTo(Produto.class);
        if (produto != null) {
          if (dbServico.produtoJaCadastrado(produto)) {
            dbServico.registraVenda(produto);
            ctx.response().setStatusCode(HttpResponseStatus.OK.code()).end();
          } else {
            ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end(e.getMessage());
    }
  }

  public void getTodosProdutosVendidos(RoutingContext ctx) {
    try {
      var array = new JsonArray(dbServico.listarProdutosVendidos());
      ctx.response().setStatusCode(200).end(array.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
