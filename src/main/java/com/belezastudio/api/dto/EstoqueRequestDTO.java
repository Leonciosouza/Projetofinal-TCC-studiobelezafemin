package com.belezastudio.api.dto;

public record EstoqueRequestDTO(
        String nomeProduto,
        Integer quantidade,
        Integer nivelMinimo,
        String fornecedor

) { }
