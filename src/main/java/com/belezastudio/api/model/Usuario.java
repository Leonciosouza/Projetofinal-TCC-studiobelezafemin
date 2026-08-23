package com.belezastudio.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data // Anotação do Lombok que Getters, Setters, toString, etc.
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 20)
    private String perfil; // Valores: AMDIN, GERENTE, REPECPCIONISTA, PROFISSIONAL, CLIENTE.

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "pontos_fidelidade", columnDefinition = "integer default 0")
    private Integer pontosFidelidade = 0;

    // Métodos onbrigatóriso do UserDetails:
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Se o perfil estiver nulo por algum motivo, não dá erro, apenas retorna sem permissões.
        if (this.perfil == null) {
            return List.of();
        }
        // Transforma a palavra salva no banco ("PROFISSIONAL" ou "CLIENTE") em uma Autoridade do Spring.
        return List.of(new SimpleGrantedAuthority(this.perfil));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email; // O email será o "login" do usuário.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
