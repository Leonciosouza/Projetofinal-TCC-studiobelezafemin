package com.belezastudio.api.controllers;

import com.belezastudio.api.dto.*;
import com.belezastudio.api.services.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;

    // ROTA POST: Rota para registrar uma nova movimentação (Entrada/Saída).
    @PostMapping
    public ResponseEntity<?> registrarLancamento(@RequestBody FinanceiroRequestDTO dto) {
        try {
            FinanceiroResponseDTO resposta = financeiroService.registrarLancamento(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    // Rota GET: Recupera todo o histórico financeiro persistido
    @GetMapping
    public ResponseEntity<List<FinanceiroResponseDTO>> listarTodos() {
        return ResponseEntity.ok(financeiroService.listarTodos());
    }

    // Rota DELETE: Permite estornar fisicamente uma linha pelo ID do lançamento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            financeiroService.deletarLancamento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // NOVA ROTA GET: (Mapeada via RequestParam para fechamento de folha de pagamento).
    // Exemplo de chamada no Postman: GET http://localhost:8080/api/financeiro/comissao/4?mes=6&ano=2026
    @GetMapping("/comissao/{idProfissional}")
    public ResponseEntity<?> calcularFechamentoComissao(
            @PathVariable Long idProfissional,
            @RequestParam int mes,
            @RequestParam int ano) {
        try {
            ComissaoResponseDTO relatorio = financeiroService.calcularComissaoMensal(idProfissional, mes, ano);
            return ResponseEntity.ok(relatorio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ROTA GERENCIAL 1: Obter o Lucro ou Prejuízo do Mês (Receitas - Despesas).
    // Exemplo: GET http://localhost:8080/api/finaceiro/balanco?mes=6&ano=2026
    @GetMapping("/balanco")
    public ResponseEntity<BalancoMensalResponseDTO> obterBlancoMensal(
            @RequestParam int mes,
            @RequestParam int ano) {
        return ResponseEntity.ok(financeiroService.gerarBalancoMensal(mes, ano));
    }

    // ROTA GERENCIAL 2: Obter o Fechamento de Caixa de um Dia Específico (Receitas - Despesas).
    // Exemplo: GET http://localhost:8080/api/financeiro/fechamento-diario?data=2026-06-27
    @GetMapping("/fechamento-diario")
    public ResponseEntity<FechamentoDiarioResponseDTO> obterFechamentoDiario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(financeiroService.gerarFechamentoDiario(data));
    }


}
