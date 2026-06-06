package com.belezastudio.api.controllers;

import com.belezastudio.api.dto.AgendamentoRequestDTO;
import com.belezastudio.api.dto.AgendamentoResponseDTO;
import com.belezastudio.api.model.Agendamento;
import com.belezastudio.api.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    // Endpoint para criar um novo Agendamento (Acesso livre para autoagendamento[cite: 41]).
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody AgendamentoRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.cadastrarAgendamento(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para listar todos os agendamentos.
    @GetMapping
    public ResponseEntity<List<AgendamentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(agendamentoService.listarTodos());
    }

    // Endpoint para buscar um agendamento específico pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(agendamentoService.buscarPorID(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ROTA específica para mudar o status (PATCH em vez de PUT, pois é uma atualização parcial).
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
           String novoStatus = request.get("status");
           return ResponseEntity.ok(agendamentoService.atualizarStatus(id, novoStatus));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para deletar um agendamento pelo ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            agendamentoService.deletarAgendamento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
