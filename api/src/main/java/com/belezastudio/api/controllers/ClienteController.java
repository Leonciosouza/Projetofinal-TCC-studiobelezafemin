package com.belezastudio.api.controllers;


import com.belezastudio.api.dto.ClienteRequestDTO;
import com.belezastudio.api.dto.ClienteResponseDTO;
import com.belezastudio.api.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Endpoint para criar um novo Cliente (Acesso livre para autoagendamento[cite: 41]).
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody ClienteRequestDTO dto) {
        try {
            ClienteResponseDTO novoCliente = clienteService.cadastrarCliente(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para listar todos os clientes.
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarTodosClientes());
    }

    // Endpoint para buscar um cliente específico pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            ClienteResponseDTO cliente = clienteService.buscarPorId(id);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            // Retorna o status 404 caso o cliente não exista no banco
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Endpoint para buscar um cliente específico por ID.
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ClienteRequestDTO dto) {
        try {
            ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, dto);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para deletar um cliente existente pelo ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            clienteService.deletarCliente(id);
            // Retorna o status 204 No Content (padrão recomendado para exclusões bem-sucedidas)
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Retorna 400 Bad Request se o ID não for encontrado
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
