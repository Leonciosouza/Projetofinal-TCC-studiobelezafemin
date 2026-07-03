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
        String nomeProfissional,  // Será null se for uma despesa geral do salão; Retornado caso exista o vinculo.
        Long idAgend) // ATUALIZAÇÃO: ID do agendamento vinculado no retorno
{}

