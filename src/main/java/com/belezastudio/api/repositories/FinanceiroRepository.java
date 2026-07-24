package com.belezastudio.api.repositories;

import com.belezastudio.api.model.Financeiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FinanceiroRepository extends JpaRepository<Financeiro, Long> {

    // Busca todas as entradas de um profissional dentro de um período de datas
    @Query("SELECT f FROM Financeiro f WHERE f.profissional.idProfissional = :idProfissional " +
            "AND f.tipo = 'ENTRADA' " +
            "AND f.dataLancamento >= :inicio AND f.dataLancamento <= :fim")
    List<Financeiro> buscarEntradasPorProfissionalEPeriodo(
            @Param("idProfissional") Long idProfissional,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    // NOVA QUERY: Busca TODOS os lançamentos (Entradas e Saídas) de um período de datas, independente do profissional.
    @Query("SELECT f FROM Financeiro f WHERE f.dataLancamento >= :inicio AND f.dataLancamento <= :fim")
    List<Financeiro> buscarLancamentosPorPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
