package com.belezastudio.api.dto;

import java.math.BigDecimal;

public record ComissaoResponseDTO(
        Long idProfissional,
        String nomeProfissional,
        int mes,
        int ano,
        BigDecimal totalBrutoProduzido,
        BigDecimal porcentagemComissaoAplicada,
        BigDecimal valorComissaoReceber

) {}
