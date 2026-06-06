package com.belezastudio.api.dto;


import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoResponseDTO(
        Long idAgendamento,
        String nomeCliente,
        String nomeProfissional,
        String nomeServico,
        LocalDate dataAtendimento,
        LocalTime horaInicio,
        LocalTime horaFim,
        String status
) {}
