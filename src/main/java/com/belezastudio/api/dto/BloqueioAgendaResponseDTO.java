package com.belezastudio.api.dto;

import java.time.LocalDateTime;

public record BloqueioAgendaResponseDTO(Long idBloqueio, Long idProfissional, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String motivo) {

}
