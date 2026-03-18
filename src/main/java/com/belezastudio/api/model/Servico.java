package com.belezastudio.api.model;


import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name= "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServico;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;

    @Column(name = "preco_padrao", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoPadrao;


    // Relacionamento Muitos para Um: Vários serviços podem pertencer a um profissional.


    @ManyToOne
    @JoinColumn(name = "id_profissional" , nullable = false)
    private Profissional profissional;



}
