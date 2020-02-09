package br.com.teste.stock_mzk;

import br.com.teste.stock_mzk.handler.FalhaHandler;
import br.com.teste.stock_mzk.handler.ProdutoHandler;
import br.com.teste.stock_mzk.handler.ValidacaoHandler;
import br.com.teste.stock_mzk.service.DbServico;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {
  public final String APPLICATION_JSON = "application/json";
  Logger log = Logger.getLogger(getClass().getName());

  @Override
  public void start(Promise<Void> startPromise) {
    iniciaBancoDeDados();
    var produtoHandler = new ProdutoHandler();
    var validacaoHandler = new ValidacaoHandler();
    var falhaHandler =  new FalhaHandler();

    var router = Router.router(vertx);
    router.get("/api/estoque")
      .handler(produtoHandler::getTodosProdutosEmEstoque)
      .produces(APPLICATION_JSON);

    router.get("/api/vendas")
      .handler(produtoHandler::getTodosProdutosVendidos)
      .produces(APPLICATION_JSON);

    router.post().handler(BodyHandler.create());
    router.post("/api/salvar")
      .handler(validacaoHandler.validacaoSalvar())
      .handler(produtoHandler::salvarProduto)
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .failureHandler(falhaHandler::erro);

    router.delete().handler(BodyHandler.create());
    router.delete("/api/apagar")
      .handler(validacaoHandler.validacaoDelete())
      .handler(produtoHandler::deleteProduto)
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .failureHandler(falhaHandler::erro);

    router.post("/api/vender")
      .handler(validacaoHandler.valdacaoVenda())
      .handler(produtoHandler::venderProduto)
      .consumes(APPLICATION_JSON)
      .produces(APPLICATION_JSON)
      .failureHandler(falhaHandler::erro);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080, result -> {
        if (result.succeeded()) {
          startPromise.complete();
          log.info("Servidor Startado!");
        } else {
          startPromise.fail(result.cause());
        }
      });
  }

  public void iniciaBancoDeDados() {
    DbServico.getInstance();
  }

  public static void main(String[] args) {

    var vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
