package com.belezastudio.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "estoque")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;

    @Column(name = "nome_produto", nullable = false, length = 100)
    private String nomeProduto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "nivel_minimo", nullable = false)
    private Integer nivelMinimo;

    @Column(length = 100)
    private String fornecedor;

    @Column(name = "data_ultima_compra")
    private LocalDate dataUltimaCompra;

}
