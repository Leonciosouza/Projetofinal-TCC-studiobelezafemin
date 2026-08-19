package com.belezastudio.api.dto;

import java.math.BigDecimal;

public record ProfissionalResponseDTO (
        Long idProfissional,
        Long idUsuario,
        String nome,
        String email,
        String telefone,
        String funcao,
        BigDecimal porcentagemComissao
) {}

