package com.belezastudio.api.repositories;

import com.belezastudio.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Retorna todos os produtos cuja quantidade atual é <= ao nível minimo.
    @Query("SELECT p FROM Produto p WHERE p.quantidade <= p.nivelMinimo")
    List<Produto> findProdutosComEstoqueBaixo();

}
