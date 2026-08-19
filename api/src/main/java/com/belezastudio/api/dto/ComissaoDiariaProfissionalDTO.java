package com.belezastudio.api.dto;

import java.math.BigDecimal;

public record ComissaoDiariaProfissionalDTO(
        Long idProfissional,
        String nomeProfissional,
        String totalProduzido, // Mudou de BigDecimal para String.
        String comissaoDevida // Mudou de BigDecimal para String.
) {

}
