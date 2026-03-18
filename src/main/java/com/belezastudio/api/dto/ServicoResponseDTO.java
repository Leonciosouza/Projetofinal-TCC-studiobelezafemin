package com.belezastudio.api.dto;

import java.math.BigDecimal;

public record ServicoResponseDTO (
    Long idServico,
    String descricao,
    Integer duracaoMinutos,
    BigDecimal precoPadrao,
    Long idProfissional,
    String nomeProfissional // Retornamos o nome para facilitar na tela do cliente.
){}
