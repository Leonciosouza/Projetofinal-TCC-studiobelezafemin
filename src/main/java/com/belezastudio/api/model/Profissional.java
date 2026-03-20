package com.belezastudio.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "profissionais")
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfissional;

    // Relacionamento 1 para 1 com a tabela de Usuários.
    // CascadeType.ALL permite salvar ou deletar o usuário automaticamente ao maipular o profissional.
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuário", referencedColumnName = "idUsuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String funcao;

    @Column(name = "porcentagem_comissao", precision = 5, scale = 2)
    private BigDecimal porcentagemComissao;
}
