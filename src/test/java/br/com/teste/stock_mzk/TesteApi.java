package br.com.teste.stock_mzk;

import br.com.teste.stock_mzk.model.Produto;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(VertxExtension.class)

public class TesteApi {
  final String SALVAR = "/api/salvar";
  final String VENDER = "/api/vender";
  final String LISTAR_ESTOQUE = "/api/estoque";
  final String DELETE = "/api/apagar";
  final String LISTAR_VENDAS = "/api/vendas";

  @BeforeAll
  static void before(Vertx vertx, VertxTestContext context){
    System.out.println("Before");
    vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle(), context.succeeding(id -> context.completeNow()));
  }

  @Test
  @DisplayName("Verifica lista como retorno do endPoint")
  void verificaListaComoRetornoDoEndpoint(Vertx vertx, VertxTestContext ctx) {
    vertx.createHttpClient().getNow(8080, "localhost", LISTAR_ESTOQUE, res -> {
      res.bodyHandler( body -> {
        ctx.verify(() -> assertEquals(true, body.toJsonArray() != null));
        ctx.completeNow();
      });
    });
  }

  @Test
  @DisplayName("Insere Produto no estoque")
  void insereProdutoNoEstoque(Vertx vertx, VertxTestContext ctx) throws Throwable {
    final String json = Json.encodePrettily(new Produto("Meu nome", "7899023i", "122"));
    vertx.createHttpClient().post(8080, "localhost", SALVAR)
      .putHeader("content-type", "application/json")
      .putHeader("content-length", Integer.toString(json.length()))
      .handler(response -> {
        assertEquals(response.statusCode(), HttpResponseStatus.CREATED.code());
        response.bodyHandler(body -> {
          final Produto meu = Json.decodeValue(body.toString(), Produto.class);
          assertEquals(meu.getNome(), "Meu nome");
          assertEquals(meu.getCodigoBarras(), "7899023i");
          assertEquals(meu.getNumeroSerie(), "122");

          vertx.createHttpClient().getNow(8080, "localhost", LISTAR_ESTOQUE, res -> {
            res.bodyHandler(bod -> {
              ctx.verify(() -> assertEquals(true, bod.toJsonArray().size() == 1));
              ctx.completeNow();
            });
          });
        });
      })
      .write(json)
      .end();
  }

  @Test
  @DisplayName("Tenta inserir produto no estoque sem nome")
  void tentaInserirProdutoSemNome(Vertx vertx, VertxTestContext ctx) throws Throwable {
    var produto = new Produto("789213123", "123");
    final String json = Json.encodePrettily(produto);
    vertx.createHttpClient().post(8080, "localhost", SALVAR)
      .putHeader("content-type", "application/json")
      .putHeader("content-length", Integer.toString(json.length()))
      .handler(response -> {
        ctx.verify(() -> assertEquals(response.statusCode(), HttpResponseStatus.BAD_REQUEST.code()));
        ctx.completeNow();
      })
      .write(json)
      .end();
  }

  @Test
  @DisplayName("Tenta vender um produto que não esta cadastrado")
  void tentaVenderProdutoQueNaoExiste(Vertx vertx, VertxTestContext ctx) {
    var produto = new Produto("000", "001");
    final String json = Json.encodePrettily(produto);
    vertx.createHttpClient().post(8080, "localhost", VENDER)
      .putHeader("content-type", "application/json")
      .putHeader("content-length", Integer.toString(json.length()))
      .handler(response -> {
        assertEquals(response.statusCode(), HttpResponseStatus.BAD_REQUEST.code());
        ctx.completeNow();
      })
      .write(json)
      .end();
  }

  @Test
  @DisplayName("Cadastra e vende um produto")
  void cadastraEVendeProdutoCadastrado(Vertx vertx, VertxTestContext ctx) {
    final String json = Json.encodePrettily(new Produto("Produto Vendido", "000", "0001"));
    vertx.createHttpClient().post(8080, "localhost", SALVAR)
      .putHeader("content-type", "application/json")
      .putHeader("content-length", Integer.toString(json.length()))
      .handler(response -> {
        assertEquals(HttpResponseStatus.CREATED.code(), response.statusCode());
        response.bodyHandler(body -> {

        });
      })
      .write(json)
      .handler(r -> {
        var produto = new Produto("000", "0001");
        final String jsonProduto = Json.encodePrettily(produto);
        vertx.createHttpClient().post(8080, "localhost", VENDER)
          .putHeader("content-type", "application/json")
          .putHeader("content-length", Integer.toString(jsonProduto.length()))
          .handler(res -> {
            ctx.verify(() -> assertEquals(HttpResponseStatus.OK.code(), res.statusCode()));
            ctx.completeNow();
          })
          .write(jsonProduto)
          .handler( t -> {
            vertx.createHttpClient().getNow(8080, "localhost", LISTAR_VENDAS, res -> {
              res.bodyHandler( body -> {
                ctx.verify(() -> assertEquals(HttpResponseStatus.OK.code(), res.statusCode()));
                ctx.verify(() -> assertEquals(true, body.toJsonArray().size() > 0));
                ctx.completeNow();
              });
            });
          })
          .end();
      })
      .end();
  }

  @Test
  @DisplayName("Apaga um produto do estoque")
  void deletandoProdutoCadastradoNoEstoque(Vertx vertx, VertxTestContext ctx) {
    final var json = Json.encodePrettily(new Produto("Produto Delete", "789789", "0001"));
    vertx.createHttpClient().post(8080, "localhost", SALVAR)
      .putHeader("content-type", "application/json")
      .putHeader("content-length", Integer.toString(json.length()))
      .handler(response -> {
        assertEquals(HttpResponseStatus.CREATED.code(), response.statusCode());
        response.bodyHandler(body -> {
          final Produto meu = Json.decodeValue(body.toString(), Produto.class);
          assertEquals(meu.getNome(), "Meu Delete");
          assertEquals(meu.getCodigoBarras(), "789789");
          assertEquals(meu.getNumeroSerie(), "0001");
        });
      })
      .write(json)
      .handler(r -> {
        //var jsonDelete = Json.encodePrettily(new Produto("Produto Delete", "789789zzzzz", "0001"));
        vertx.createHttpClient().delete(8080, "localhost", DELETE)
          .putHeader("content-type", "application/json")
          .putHeader("content-length", Integer.toString(json.length()))
          .handler(res -> {
            ctx.verify(() -> assertEquals(HttpResponseStatus.OK.code(), res.statusCode()));
            ctx.completeNow();
          })
          .write(json)
          .end();
      })
      .end();
  }

  @Test
  @DisplayName("Tenta apagar um produto que não está cadastrado")
  void tentaDeletarProdutoQueNaoExiste(Vertx vertx, VertxTestContext ctx) {
    var jsonDelete = Json.encodePrettily(new Produto("Produto Delete", "789789zzzzz", "0001"));
    vertx.createHttpClient().delete(8080, "localhost", DELETE)
      .putHeader("content-type", "application/json")
      .putHeader("content-length", Integer.toString(jsonDelete.length()))
      .handler(res -> {
        assertEquals(HttpResponseStatus.NOT_FOUND.code(), res.statusCode());
        ctx.completeNow();
      })
      .write(jsonDelete)
      .end();
  }

  @Test
  @DisplayName("Lista os produtos vendidos")
  void testaEndpiontDeProdutosVendidos(Vertx vertx, VertxTestContext ctx) {
    vertx.createHttpClient().getNow(8080, "localhost", LISTAR_VENDAS, res -> {
      res.bodyHandler( body -> {
        ctx.verify(() -> assertEquals(HttpResponseStatus.OK.code(), res.statusCode()));
        ctx.verify(() -> assertEquals(true, body.toJsonArray() != null));
        ctx.completeNow();
      });
    });
  }

  @AfterAll
  static void after(Vertx vertx, VertxTestContext context){
    vertx.close();
    context.completeNow();
    System.out.println("After");
  }

}
