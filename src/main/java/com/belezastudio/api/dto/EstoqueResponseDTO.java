package com.belezastudio.api.dto;

import java.time.LocalDate;

public record EstoqueResponseDTO(
        Long idProduto,
        String nomeProduto,
        Integer quantidade,
        Integer nivelMinimo,
        String fornecedor,
        LocalDate dataUltimaCompra,
        String statusReposicao // Informará se o estoque está "OK" ou "BAIXO".
) { }
