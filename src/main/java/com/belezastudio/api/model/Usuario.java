package com.belezastudio.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data // Anotação do Lombok que Getters, Setters, toString, etc.
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 20)
    private String perfil; // Valores: AMDIN, GERENTE, REPECPCIONISTA, PROFISSIONAL, CLIENTE.

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "pontos_fidelidade", columnDefinition = "integer default 0")
    private Integer pontosFidelidade = 0;

}
