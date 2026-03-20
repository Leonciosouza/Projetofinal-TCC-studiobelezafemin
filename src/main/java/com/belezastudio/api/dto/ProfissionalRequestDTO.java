package com.belezastudio.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProfissionalRequestDTO (

    // Dados do Usuário.
    String nome,
    String email,
    String telefone,
    String login,
    String senha,
    LocalDate dataNascimento,

    // Dados do Profissional
    String funcao,
    BigDecimal porcentagemComissao

) {}
