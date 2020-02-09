package br.com.teste.stock_mzk.handler;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler;

import java.util.HashMap;

public class ValidacaoHandler {
  public HTTPRequestValidationHandler validacaoSalvar() {
    var regexCodigoBarras = new HashMap<>();
    regexCodigoBarras.put("type", "number");
    regexCodigoBarras.put("pattern", "[0-9]+");

    var map = new HashMap<String, Object>();
    map.put("nome", "string");
    map.put("codigoBarras", regexCodigoBarras);
    map.put("numeroSerie", "string");
    map.put("type", "object");

    var schema = JsonObject.mapFrom(map).toString();
    return HTTPRequestValidationHandler.create()
      .addJsonBodySchema(schema);
  }

  public HTTPRequestValidationHandler validacaoDelete() {
    var map = new HashMap<String, String>();
    map.put("codigoBarras", "string");
    map.put("numeroSerie", "string");
    map.put("type", "object");

    var schena = JsonObject.mapFrom(map).toString();
    return  HTTPRequestValidationHandler.create()
      .addJsonBodySchema(schena);
  }

  public HTTPRequestValidationHandler valdacaoVenda() {
      var map = new HashMap<String, String>();
      map.put("codigoBarras", "string");
      map.put("numeroSerie", "string");
      map.put("type", "object");

      var schena = JsonObject.mapFrom(map).toString();
      return HTTPRequestValidationHandler.create()
        .addJsonBodySchema(schena);
  }
}
