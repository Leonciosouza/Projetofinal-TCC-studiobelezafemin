package com.belezastudio.api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;

    @Column(name = "nome_produto", nullable = false)
    private String nomeProduto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "nivel_minimo", nullable = false)
    private Integer nivelMinimo;

    @Column(name = "fornecedor", length = 100)
    private String fornecedor;

    @Column(name = "data_ultima_compra")
    private LocalDate dataUltimaCompra;

    // Construtor Automático para data de compra incial.
    @PrePersist
    protected void onCreate() {
        if (this.dataUltimaCompra == null) {
            this.dataUltimaCompra = LocalDate.now();
        }
    }

    // --- GETTERS E SETTERS ---
    public Long getIdProduto() {
        return idProduto;
    }
    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }
    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getNivelMinimo() {
        return nivelMinimo;
    }
    public void setNivelMinimo(Integer nivelMinimo) {
        this.nivelMinimo = nivelMinimo;
    }

    public String getFornecedor() {
        return fornecedor;
    }
    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public LocalDate getDataUltimaCompra() {
        return  dataUltimaCompra;
    }
    public void setDataUltimaCompra(LocalDate dataUltimaCompra) {
        this.dataUltimaCompra = dataUltimaCompra;
    }
}
