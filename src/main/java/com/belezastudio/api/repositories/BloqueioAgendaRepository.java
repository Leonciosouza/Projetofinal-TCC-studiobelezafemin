package com.belezastudio.api.repositories;

import com.belezastudio.api.model.BloqueioAgenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BloqueioAgendaRepository extends JpaRepository<BloqueioAgenda, Long> {

    // Regra metemática: Verifica se o horário solicitado "bate" com algum bloqueio existente.
    @Query("SELECT COUNT(b) > 0 FROM BloqueioAgenda b WHERE " +
            "(b.profissional.idProfissional = :idProfissional OR b.profissional IS NULL) AND " +
            "(b.dataHoraInicio < :fim AND b.dataHoraFim > :inicio)")
    boolean existeBloqueioNoHorario(@Param("idProfissional") Long idProfissional,
                                    @Param("inicio") LocalDateTime inicio,
                                    @Param("fim") LocalDateTime fim);
}
