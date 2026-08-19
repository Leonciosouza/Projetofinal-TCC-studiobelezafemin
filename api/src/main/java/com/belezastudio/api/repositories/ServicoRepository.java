package com.belezastudio.api.repositories;

import com.belezastudio.api.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    //Método customizado que o Spring Data cria automaticamente pela nomencaltura passada.
    List<Servico> findByProfissionalIdProfissional(Long idProfissional);
}
