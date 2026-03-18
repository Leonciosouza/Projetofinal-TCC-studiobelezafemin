package com.belezastudio.api.dto;

import java.math.BigDecimal;

public record ServicoRequestDTO (

    String nome,
    String descrição,
    Integer duracaoMinutos,
    BigDecimal precoPadrao,
    Long idProfissional // Recebe apenas o ID para fazer o vínculo no banco.
) {
    public String descricao() {
        return "olá";
    }
}
