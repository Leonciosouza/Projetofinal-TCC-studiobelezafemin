package com.belezastudio.api.controllers;

import com.belezastudio.api.dto.BloqueioAgendaRequestDTO;
import com.belezastudio.api.dto.BloqueioAgendaResponseDTO;
import com.belezastudio.api.services.BloqueioAgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bloqueios")
public class BloqueioAgendaController {

    @Autowired
    private BloqueioAgendaService bloqueioService;

    @PostMapping
    public ResponseEntity<BloqueioAgendaResponseDTO> criar(@RequestBody BloqueioAgendaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bloqueioService.criarBloqueio(dto));
    }
}
