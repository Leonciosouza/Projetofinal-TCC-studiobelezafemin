package com.belezastudio.api.dto;

import java.time.LocalDate;

public record ClienteRequestDTO (
        String nome,
        String email,
        String telefone,
        // String login,
        String senha,
        LocalDate dataNascimento
) {}
