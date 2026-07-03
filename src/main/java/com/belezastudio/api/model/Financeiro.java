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
    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo; // Armazena estritamente "ENTRADA" ou "SAÍDA".

    // O atributo 'valor' foi adicionado como BigDecimal com a precisão correta para dinheiro.
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    // Ex.: Dinheiro, Cartão de Crédito, Pix, Fiado.
    @Column(name = "forma_pagamento", length = 50)
    private String formaPagamento;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_lancamento", nullable = false, updatable = false)
    private LocalDateTime dataLancamento;

    // Relacionamento opcional: Despesas fixas (luz, aluguel) não têm profissional vinculado.
    @ManyToOne
    @JoinColumn(name = "id_profissional")
    private Profissional profissional;

    // Adicionando novo relacionamento na classe.
    @ManyToOne
    @JoinColumn(name = "id_agendamento")
    private Agendamento agendamento;

    // Configura a data automaticamente caso não seja enviada.
    @PrePersist
    protected void onCreate() {
        if (dataLancamento == null) {
            dataLancamento = LocalDateTime.now();
        }
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

    // Getters e Setters (Você pode gerar pelo IntelliJ ou usar Lombok se estiver configurado).
    public Long getIdLancamento() {
        return idLancamento;
    }

    public void setIdLancamento(Long idLancamento) {
        this.idLancamento = idLancamento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDateTime dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    // Adicionando os Getters e Setters correpondentes.
    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }
}
