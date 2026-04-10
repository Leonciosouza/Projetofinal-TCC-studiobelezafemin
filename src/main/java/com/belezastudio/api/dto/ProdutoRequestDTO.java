package com.belezastudio.api.dto;

public record ProdutoRequestDTO (
        String nomeProduto,
        Integer quantidade,
        Integer nivelMinimo,
        String fornecedor

) {
    public Integer quantidadeInicial() {
        return 0;
    }

    public Integer nivelMinimo() {
        return 0;
    }

    public String fornecedor() {
        return "";
    }
}
