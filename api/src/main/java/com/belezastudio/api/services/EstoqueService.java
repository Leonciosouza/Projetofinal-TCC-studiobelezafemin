package com.belezastudio.api.services;


import com.belezastudio.api.dto.*;
import com.belezastudio.api.model.Estoque;
import com.belezastudio.api.repositories.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    // CADASTRAR NOVO PRODUTO NO ESTOQUE.
    public EstoqueResponseDTO cadastrarProduto(EstoqueResponseDTO dto) {
        Estoque estoque = new Estoque();
        estoque.setNomeProduto(dto.nomeProduto());
        estoque.setQuantidade(dto.quantidade());
        estoque.setNivelMinimo(dto.nivelMinimo());
        estoque.setFornecedor(dto.fornecedor());

        Estoque salvo = estoqueRepository.save(estoque);
        return converterParaDTO(salvo);
    }

    // LISTAR TODO O INVENTÁRIO DE PRODUTOS.
    public List<EstoqueResponseDTO> listarTodos() {
        return estoqueRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // FUNCIONALIDADE CHAVE: ABATER CONSUMO POR SERVIÇO (EX: Tintura, Shampoo).
    public EstoqueResponseDTO registrarConsumo(Long idProduto, ConsumoRequestDTO consumoDTO) {
        Estoque produto = estoqueRepository.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado no estoque."));

        if (produto.getQuantidade() < consumoDTO.quantidadeConsumida()) {
            throw new RuntimeException("Quantidade em estoque insuficiente para este consumo!");
        }

        // Subtrair a quantidade consumida do estoque.
        produto.setQuantidade(produto.getQuantidade() - consumoDTO.quantidadeConsumida());

        Estoque atualizado = estoqueRepository.save(produto);
        return converterParaDTO(atualizado);
    }

    // ADICIONAR REPOSIÇÃO (A Trigger no PostgreSQL atualizará a 'data_ultima_compra' automaticamente).
    public EstoqueResponseDTO registrarReposicao(Long idProduto, int quantidadeComprada) {
        Estoque produto = estoqueRepository.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        produto.setQuantidade(produto.getQuantidade() + quantidadeComprada);
        Estoque atualizado = estoqueRepository.save(produto);

        return converterParaDTO(atualizado);
    }

    // ALERTA DE REPOSIÇÃO: Listar apenas o que está acabando no estoque (quantidade < nível mínimo).
    public List<EstoqueResponseDTO> listarAlertasReposicao() {
        return estoqueRepository.buscarProdutosComEstoqueBaixo().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // UTILITÁRIO PRIVADO: Converte e insere o selo visual de status de estoque baixo.
    private EstoqueResponseDTO converterParaDTO(Estoque e) {
        String status = (e.getQuantidade() <= e.getNivelMinimo()) ? "ALERTA: ESTOQUE BAIXO" : "OK";

        return new EstoqueResponseDTO(
                e.getIdProduto(),
                e.getNomeProduto(),
                e.getQuantidade(),
                e.getNivelMinimo(),
                e.getFornecedor(),
                e.getDataUltimaCompra(),
                status
        );
    }

}


