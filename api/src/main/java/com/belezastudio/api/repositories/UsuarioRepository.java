package com.belezastudio.api.repositories;

import com.belezastudio.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Métodos mágicos do Spring Data;
    // Único método extra necessário para o Spring Security validar o login
    UserDetails findByEmail(String email);
    // List<Usuario> findByPerfil(String perfil); // Útil para listar apenas quem tem o perfil 'CLIENTE'.
}
