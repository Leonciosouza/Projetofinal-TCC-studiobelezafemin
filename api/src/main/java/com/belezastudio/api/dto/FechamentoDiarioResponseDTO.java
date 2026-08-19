package com.belezastudio.api.dto;

// import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FechamentoDiarioResponseDTO(
        LocalDate data,
        String faturamentoBrutoDia, // Mudou de BigDecimal para String.
        String totalComisoesDia, // Mudou de BigDecimal para String.
        String saldoRetidoSalao, // O que sobra no caixa do salão após pagar os profissinais e o valor retido para o salão.
        List<ComissaoDiariaProfissionalDTO> detalhamentoProfissionais
) {
}
