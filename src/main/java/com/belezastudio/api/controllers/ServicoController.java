package com.belezastudio.api.controllers;

import com.belezastudio.api.dto.ServicoRequestDTO;
import com.belezastudio.api.dto.ServicoResponseDTO;
import com.belezastudio.api.services.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody ServicoRequestDTO dto) {
        try {
            ServicoResponseDTO response = servicoService.cadastrarServico(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        }   catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    // Rota essencial para o Frontend na hora de agendar.
    @GetMapping("/api/profissional/{idProfissional}")
    public ResponseEntity<List<ServicoResponseDTO>> listarPorProfissional(@PathVariable Long idProfissional) {
        return ResponseEntity.ok(servicoService.listarPorProfisssional(idProfissional));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){
        try {
            return ResponseEntity.ok(servicoService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ServicoRequestDTO dto) {
       try {
           return ResponseEntity.ok(servicoService.atualizarServico(id, dto));
       } catch (RuntimeException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            servicoService.deletarServico(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
