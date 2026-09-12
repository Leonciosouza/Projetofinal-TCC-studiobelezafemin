package com.belezastudio.api.repositories;

import com.belezastudio.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Métodos mágicos do Spring Data;

    UserDetails findByEmail(String email);
    // List<Usuario> findByPerfil(String perfil); // Útil para listar apenas quem tem o perfil 'CLIENTE'.

    // NOVA LÓGICA DO ROBÔ: Busca clientes filtrando apenas pelo dia e mês de nascimento
    @Query("SELECT u FROM Usuario u WHERE MONTH(u.dataNascimento) = MONTH(:hoje) AND DAY(u.dataNascimento) = DAY(:hoje) AND u.perfil = 'CLIENTE'")
    List<Usuario> findAniversariantesDoDia(@Param("hoje")LocalDate hoje);
}
