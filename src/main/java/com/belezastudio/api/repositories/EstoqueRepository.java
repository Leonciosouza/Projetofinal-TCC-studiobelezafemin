package com.belezastudio.api.repositories;


import com.belezastudio.api.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    // Query Inteligente: Busca todos os produtos cuja quantidade seja menor ou igual ao nível mínimo definido.
    @Query("SELECT e FROM Estoque e WHERE e.quantidade <= e.nivelMinimo")
    List<Estoque> buscarProdutosComEstoqueBaixo();
}
