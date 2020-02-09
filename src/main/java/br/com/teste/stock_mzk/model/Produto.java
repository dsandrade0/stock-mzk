package br.com.teste.stock_mzk.model;

import java.util.Objects;

public class Produto {

  private String nome;
  private String codigoBarras;
  private String numeroSerie;

  public Produto(String codigoBarras, String numeroSerie) {
    this.numeroSerie = numeroSerie;
    this.codigoBarras = codigoBarras;
  }

  public Produto() {

  }

  public Produto(String nome, String codigoBarras, String numeroSerie) {
    this.nome = nome;
    this.codigoBarras = codigoBarras;
    this.numeroSerie = numeroSerie;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Produto produto = (Produto) o;
    return Objects.equals(codigoBarras, produto.codigoBarras) &&
      Objects.equals(numeroSerie, produto.numeroSerie);
  }

  @Override
  public int hashCode() {
    return Objects.hash(codigoBarras, numeroSerie);
  }

  public boolean validar() {
    if (this.nome == null || this.nome.isEmpty()) {
      return false;
    }

    if (this.codigoBarras == null || this.codigoBarras.isEmpty()) {
      return false;
    }

    if (this.numeroSerie == null || this.numeroSerie.isEmpty()) {
      return false;
    }
    return true;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getCodigoBarras() {
    return codigoBarras;
  }

  public void setCodigoBarras(String codigoBarras) {
    this.codigoBarras = codigoBarras;
  }

  public String getNumeroSerie() {
    return numeroSerie;
  }

  public void setNumeroSerie(String numeroSerie) {
    this.numeroSerie = numeroSerie;
  }
}
