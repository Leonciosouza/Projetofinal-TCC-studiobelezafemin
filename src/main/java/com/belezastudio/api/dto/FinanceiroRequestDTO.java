package com.belezastudio.api.dto;

import java.math.BigDecimal;

public record FinanceiroRequestDTO (
        String tipo, // "ENTRADA" OU "SAÍDA".
        BigDecimal valor,
        String formaPagamento,  //"Pix", "Cartão", "Dinheiro", etc.
        String descricao,
        Long idProfissional // Pode ser null para despesas gerais.
) {}


