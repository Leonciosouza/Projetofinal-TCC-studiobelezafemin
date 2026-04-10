package com.belezastudio.api.controllers;

import com.belezastudio.api.dto.MovimentacaoEstoqueDTO;
import com.belezastudio.api.dto.ProdutoRequestDTO;
import com.belezastudio.api.dto.ProdutoResponseDTO;
import com.belezastudio.api.services.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    // Cadastrar novo produto no estoque.
    @PostMapping
    public ResponseEntity<?> cadastrarProduto(@RequestBody ProdutoRequestDTO dto) {
        try {
            ProdutoResponseDTO response = estoqueService.cadastrarProduto(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Listar todos os produtos do estoque.
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(estoqueService.listarTodos());
    }

    // Rota para Dashboard: Listar produtos que precisam ser comprados (estoque baixo).
    @GetMapping("/alerta")
    public ResponseEntity<List<ProdutoResponseDTO>> listarEstoqueBaixo() {
        return ResponseEntity.ok(estoqueService.listarEstoqueBaixo());
    }

    // Rota de Ação: Dar entrada ou saída em um produto específico.
    @PostMapping("/{id}/movimentar")
    public ResponseEntity<?> movimentarEstoque(
            @PathVariable Long id,
            @RequestBody MovimentacaoEstoqueDTO dto) {
        try {
            ProdutoResponseDTO response = estoqueService.movimentarEstoque(id, dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable Long id) {
        try {
            estoqueService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }
    }
}
