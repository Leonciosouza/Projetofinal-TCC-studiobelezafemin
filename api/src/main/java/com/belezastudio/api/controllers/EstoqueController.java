package com.belezastudio.api.controllers;

import com.belezastudio.api.dto.*;
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
    public ResponseEntity<EstoqueResponseDTO> cadastrarProduto(@RequestBody EstoqueResponseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estoqueService.cadastrarProduto(dto));
    }

    // Listar todos os produtos do estoque.
    @GetMapping
    public ResponseEntity<List<EstoqueResponseDTO>> listarInventario() {
        return ResponseEntity.ok(estoqueService.listarTodos());
    }

    // ROTA PARA O FRONTEND ABATER O PRODUTO APÓS UM SERVIÇO SER REALIZADO, PARA NÃO TER QUE USAR O PATCH.
    // EX.: PUT http://localhost:8080/api/estoque/1/consumir
    @PutMapping("/{id}/consumir")
    public ResponseEntity<?> registrarConsumoNoServico(
            @PathVariable Long id,
            @RequestBody ConsumoRequestDTO consumoDTO) {

        try {
            return ResponseEntity.ok(estoqueService.registrarConsumo(id, consumoDTO));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ROTA PARA DAR ENTRADA EM NOTA FISCAL/COMPRAS.
    // EX.: PUT http://localhost:8080/api/estoque/1/repor?quantidade=10
    @PutMapping("/{id}/repor")
    public ResponseEntity<?> registrarReposicao(
            @PathVariable Long id,
            @RequestParam int quantidade) {
        try {
            return ResponseEntity.ok(estoqueService.registrarReposicao(id, quantidade));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ROTA GERENCIAL: Retorna apenas o que precisa ser comprado urgente.
    // EX.: GET http://localhost:8080/api/estoque/alertasGET
    @GetMapping("/alertas")
    public ResponseEntity<List<EstoqueResponseDTO>> emitirRelatorioDeAlertas() {
        return ResponseEntity.ok(estoqueService.listarAlertasReposicao());
    }
    /*
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable Long id) {
        try {
            estoqueService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }
    }
    */
}
