package com.belezastudio.api.repositories;

import com.belezastudio.api.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {


    // O Spring Data JPA traduz esse nome de método automaticamente para um SELECT com WHERE.
    // Essa é a linha que o NotificacaoService vai usar para buscar a agenda do dia!
    List<Agendamento> findByDataAtendimento(LocalDate dataAtendimento);
    // Query para verificar disponibilidade e evitar duplicidade [cite: 68]
    // CORREÇÃO: Foi adicionado um espaço após o :idProfissional na primeira linha
    @Query("SELECT a FROM Agendamento a WHERE a.profissional.idProfissional = :idProfissional " +
            "AND a.dataAtendimento = :data " +
            "AND a.horaInicio < :horaFim " +
            "AND a.horaFim > :horaInicio " +
            "AND a.status != 'CANCELADO'")
    List<Agendamento> findConflitosDeHorario(
            @Param("idProfissional") Long idProfissional,
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim);

}
