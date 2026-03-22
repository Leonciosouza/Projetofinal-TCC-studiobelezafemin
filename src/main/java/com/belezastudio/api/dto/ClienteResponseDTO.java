package com.belezastudio.api.dto;

public record ClienteResponseDTO (
        Long idUsuario,
        String nome,
        String email,
        String telefone,
        Integer pontosFidelidade
) {}

