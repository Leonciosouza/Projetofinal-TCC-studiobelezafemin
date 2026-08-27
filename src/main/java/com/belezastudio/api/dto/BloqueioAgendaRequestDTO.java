package com.belezastudio.api.dto;

import java.time.LocalDateTime;

public record BloqueioAgendaRequestDTO(Long idProfissional, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String motivo) {

}
