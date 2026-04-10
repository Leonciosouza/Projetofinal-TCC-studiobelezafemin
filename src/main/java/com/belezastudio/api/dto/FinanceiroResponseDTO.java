package com.belezastudio.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceiroResponseDTO(
        Long idLancamento,
        String tipo,
        BigDecimal valor,
        String formaPagamento,
        String descricao,
        LocalDateTime dataLancamento,
        Long idProfissional,
        String nomeProfissional  // Retornado caso exista o vinculo.

) {}

