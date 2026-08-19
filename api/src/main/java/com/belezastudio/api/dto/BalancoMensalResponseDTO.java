package com.belezastudio.api.dto;

import java.math.BigDecimal;

public record BalancoMensalResponseDTO(
        int mes,
        int ano,
        BigDecimal totalEntradas,
        BigDecimal totalSaidas,
        BigDecimal saldoLiquido,
        String situacao // Retornará "LUCRO" ou "PREJUÍZO"
) { }
