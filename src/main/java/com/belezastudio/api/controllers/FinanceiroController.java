package com.belezastudio.api.controllers;

import com.belezastudio.api.dto.FinanceiroRequestDTO;
import com.belezastudio.api.dto.FinanceiroResponseDTO;
import com.belezastudio.api.services.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;

    // Rota para registrar uma nova movimentação (Entrada/Saída).
    @PostMapping("/lancamento")
    public ResponseEntity<?> registrarLancamento(@RequestBody FinanceiroRequestDTO dto) {
        try {
            FinanceiroResponseDTO response = financeiroService.registrarLancamento(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Rota para puxar o fluxo de caixa apenas do dia atual.
    @GetMapping("/caixa-diario")
    public ResponseEntity<List<FinanceiroResponseDTO>> listarCaixaDoDia() {
        return ResponseEntity.ok(financeiroService.listarCaixaDoDia());
    }

    // Rota estratégica: Retorna o valor exato que o salão deve pagar de comissão ao profissional no mês atual, considerando os serviços realizados e as regras de comissão definidas.
    @GetMapping("/comissao/{idProfissional}")
    public ResponseEntity<?> calcularComissao(@PathVariable Long idProfissional)  {
        try {
            BigDecimal comissao = financeiroService.calcularComissaoMensal(idProfissional);
            return ResponseEntity.ok("O valor da comissão atual é de: R$ " + comissao.toString());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
