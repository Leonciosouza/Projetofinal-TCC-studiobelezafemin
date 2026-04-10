package com.belezastudio.api.dto;

import java.time.LocalDate;

public record ProdutoResponseDTO (
        Long idProduto,
        String nomeProduto,
        Integer quantidade,
        Integer nivelMinimo,
        String fornecedor,
        LocalDate dataUltimaCompra,
        boolean alertaEstoqueBaixo // Campo calculado para facilitar o Frontend.


) {}
