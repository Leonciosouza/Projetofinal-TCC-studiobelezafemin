package com.belezastudio.api.repositories;

import com.belezastudio.api.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    // Caso precise buscar todos os profissionais de um cargo específico no futuro:
    // List<Profissinal> findByCargo(String cargo);
}
