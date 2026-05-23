package com.belezastudio.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "financeiro")
public class Financeiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lancamento")
    private Long idLancamento;

    // ENETRADA (pagamento de cliente) ou SAÍDA (despesas, pagamento de comissão).
    @Column(nullable = false, length = 10)
    private String tipo;

    // O atributo 'valor' foi adicionado como BigDecimal com a precisão correta para dinheiro.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    // Ex.: Dinheiro, Cartão de Crédito, Pix, Fiado.
    @Column(name = "forma_pagamento", length = 50)
    private String formaPagamento;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_lancamento", nullable = false, updatable = false)
    private LocalDateTime dataLancamento;

    // Relacionamento opcional: Despesas fixas (luz, aluguel) não têm profissional vinculado.
    @ManyToOne
    @JoinColumn(name = "id_profissional")
    private Profissional profissional;

    @PrePersist
    protected void onCreate() {
        this.dataLancamento = LocalDateTime.now(); // Preenche a data automaticamente no momento do insert.
    }
    // OBSERVAÇÃO: Os métodos manuais setValor() e getValor() foram DELETADOS.
    // O @Data do Lombok já vai criar um getValor() que retorna BigDecimal perfeitamente.

    /*
    public void setValor(BigDecimal valor) {
    }

    public Object getValor() {
        return null;
    }
    */

}
