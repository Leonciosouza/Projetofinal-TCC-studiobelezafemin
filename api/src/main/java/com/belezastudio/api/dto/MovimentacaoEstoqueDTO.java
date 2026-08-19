package com.belezastudio.api.dto;

public record MovimentacaoEstoqueDTO (
        String tipoMovimentacao,// "ENTRADA" (compra) ou "SAÍDA" (consumo no salão).
        Integer quantidadeMovimentada
) {}

