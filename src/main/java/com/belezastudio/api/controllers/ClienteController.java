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
}
