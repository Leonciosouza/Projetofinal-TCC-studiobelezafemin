package com.belezastudio.api.dto;


import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoRequestDTO (
    Long idCliente,
    Long idProfissional,
    Long idServico,
    LocalDate dataAtendimento, // Formato esperado no Postman: "YYYY-MM-DD"
    LocalTime horaInicio       // Formato esperado no Postman: "HH:MM:SS"
) {}
