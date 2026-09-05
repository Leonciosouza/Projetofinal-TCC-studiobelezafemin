package com.belezastudio.api.controllers;


import com.belezastudio.api.model.Usuario;
import com.belezastudio.api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fidelidade")
public class FidelidadeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Consulta de Saldo de Pontos.
    @GetMapping("/{idCliente}/saldo")
    public ResponseEntity<String> consultarSaldo(@PathVariable Long idCliente) {
        Usuario cliente = usuarioRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        // Garante que não retorne erro se o valor no banco for nulo
        int saldo = cliente.getPontosFidelidade() != null ? cliente.getPontosFidelidade() : 0;

        return ResponseEntity.ok("Saldo atual: " + saldo + " pontos.");
    }

    // Resgate de Pontos Por Desconto:
    @PostMapping("/{idCliente}/resgatar")
    public ResponseEntity<String> resgatarRecompensa(@PathVariable Long idCliente, @RequestParam Integer pontosParaResgatar) {
        Usuario cliente = usuarioRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        int saldoAtual = cliente.getPontosFidelidade() != null ? cliente.getPontosFidelidade() : 0;

        if (saldoAtual < pontosParaResgatar) {
            return  ResponseEntity.badRequest().body("Saldo insuficiente. Você possui " + saldoAtual + " pontos.");

        }

        // Regra de negócio: O resgate mínimo é de 100 pontos.
        if (pontosParaResgatar < 100) {
            return ResponseEntity.badRequest().body("O resgate mínimo é de 100 pontos.");

        }

        // Desconta os pontos de carteira.
        cliente.setPontosFidelidade(saldoAtual - pontosParaResgatar);
        usuarioRepository.save(cliente);

        // Calcula o desconto gerado (Exemplo: a cada 100 pontos, ganha R$ 10,00).
        double valorDesconto = (pontosParaResgatar / 100) * 10.00;

        return ResponseEntity.ok("Resgate efetuado com sucesso! Você gnahou R$ " + valorDesconto +
                " de desconto no próximo serviço. Saldo restante: " + cliente.getPontosFidelidade());
    }
}
