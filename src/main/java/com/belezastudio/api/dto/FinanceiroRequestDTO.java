package com.belezastudio.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceiroRequestDTO (
        String tipo, // "ENTRADA" OU "SAÍDA".
        BigDecimal valor,
        String formaPagamento,  //"Pix", "Cartão", "Dinheiro", etc.
        String descricao,
        Long idProfissional, // Pode ser null no JSON do Postman para despesas gerais.
        String nomeProfissional,
        Long idAgendamento // Novo campo adicionado pode ser enviado como null para despesas gerais) add. no retorno da API.

) {}


