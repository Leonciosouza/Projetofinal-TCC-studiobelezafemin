package com.belezastudio.api.services;


import com.belezastudio.api.dto.MovimentacaoEstoqueDTO;
import com.belezastudio.api.dto.ProdutoRequestDTO;
import com.belezastudio.api.dto.ProdutoResponseDTO;
import com.belezastudio.api.model.Produto;
import com.belezastudio.api.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // CREATE
    public ProdutoResponseDTO cadastrarProduto(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNomeProduto(dto.nomeProduto());
        produto.setQuantidade(dto.quantidadeInicial());
        produto.setNivelMinimo(dto.nivelMinimo());
        produto.setFornecedor(dto.fornecedor());

        // Se já for cadastrado com quantidade > 0, consideramos como a primeira compra.
        if (dto.quantidadeInicial() > 0) {
            produto.setDataUltimaCompra(LocalDate.now());
        }

        Produto salvo = produtoRepository.save(produto);
        return converterParaDTO(salvo);

    }

    // READ (Todos).
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // READ (Apenas produtos com estoque baixo para relatórios).
    public List<ProdutoResponseDTO> listarEstoqueBaixo() {
        return produtoRepository.findProdutosComEstoqueBaixo().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // REGRA DE NEGÓCIO: Movimentar Estoque (Entrada/Saída)
    public ProdutoResponseDTO movimentarEstoque(Long idProduto, MovimentacaoEstoqueDTO dto) {
        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        if (dto.quantidadeMovimentada() <= 0) {
            throw new RuntimeException("A quantidade deve ser maior que zero.");
        }

        if (dto.tipoMovimentacao().equalsIgnoreCase("ENTRADA")) {
            produto.setQuantidade(produto.getQuantidade() + dto.quantidadeMovimentada());
            produto.setDataUltimaCompra(LocalDate.now()); // Atualiza a data da última compra.

        } else if (dto.tipoMovimentacao().equalsIgnoreCase("SAIDA")) {
            if (produto.getQuantidade() < dto.quantidadeMovimentada()) {
                throw new RuntimeException("Estoque insuficente para realizar esta saída.");
            }
            produto.setQuantidade(produto.getQuantidade() - dto.quantidadeMovimentada());
        } else {
            throw new RuntimeException("Tipo de movimentação inválido. Use ENTRADA ou SAÍDA.");
        }

        Produto atualizado = produtoRepository.save(produto);
        return converterParaDTO(atualizado);

    }

    // UPDATE e DELETE seguem o padrão básico...
    public void deletarProduto(Long idProduto) {
        if (!produtoRepository.existsById(idProduto)) {
            throw new RuntimeException("Produto não encontrado.");

        }
        produtoRepository.deleteById(idProduto);

    }
    // Método Utilitário Privado para conversão.
    private ProdutoResponseDTO converterParaDTO(Produto p) {
        // Regra visual: Se a quantidade for <= ao mínimo, retorna true para o alerta.
        boolean alerta = p.getQuantidade() <= p.getNivelMinimo();

        return new ProdutoResponseDTO(
                p.getIdProduto(),
                p.getNomeProduto(),
                p.getQuantidade(),
                p.getNivelMinimo(),
                p.getFornecedor(),
                p.getDataUltimaCompra(),
                alerta
        );
    }
}
