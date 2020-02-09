package br.com.teste.stock_mzk.service;

import br.com.teste.stock_mzk.model.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DbServico {
  private List<Produto> estoque;
  private List<Produto> vendas;
  private static DbServico instance;

  public Produto insere(Produto produto) {
    estoque.add(produto);
    return produto;
  }

  public static DbServico getInstance() {
    if (instance == null) {
      return new DbServico();
    } else {
      return instance;
    }
  }

  public List<Produto> listarProdutosEmEstoque() {
    return estoque;
  }

  private DbServico() {
    this.estoque = new ArrayList<>();
    this.vendas = new ArrayList<>();
  }

  public void remove(Produto produto) {
    this.estoque = this.estoque
      .stream()
      .filter(p -> !p.equals(produto))
      .collect(Collectors.toList());
  }

  public boolean existeProdutoPorNumeroSerie(String numeroSerie) {
    return estoque
      .stream()
      .filter(p -> p.getNumeroSerie().equals(numeroSerie))
      .count() > 0;
  }

  public boolean produtoJaCadastrado(Produto produto) {
    return estoque
      .stream()
      .filter( p -> p.equals(produto))
      .count() > 0
      || vendas.stream()
        .filter( p -> p.equals(produto))
        .count() > 0;
  }

  public int quantidadeDeProdutosNoEstoque() {
    return estoque.size();
  }

  public void registraVenda(Produto produto) {
    Optional<Produto> existe = estoque.stream().filter(p -> p.equals(produto)).findFirst();
    if (existe.isPresent()) {
      var produtoNaBase = existe.get();
      estoque.remove(produtoNaBase);
      vendas.add(produtoNaBase);
    }
  }

  public List<Produto> listarProdutosVendidos() {
    return vendas;
  }
}
