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

    // Busca todos os lançamentos de um período (útil para fechamento da caixa diário/mensal).
    List<Financeiro> findByDataLancamentoBetween(LocalDateTime inicio, LocalDateTime fim);

    // Busca apenas as entradas vinculadas a um profissional em um período (para calcular comissão).
    // 1. "Financeiro" escrito corretamente (com 'n').
    // 2. Espaço adicionado após o :idProfissional.
    @Query("SELECT f FROM Financeiro f WHERE f.profissional.idProfissional = :idProfissional " +
            "AND f.tipo = 'ENTRADA' " +
            "AND f.dataLancamento BETWEEN :inicio AND :fim")
    List<Financeiro> findEntradasPorProfissionalEPeriodo(
            @Param("idProfissional") Long idProfissional,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}
