package com.belezastudio.api.services;


import com.belezastudio.api.dto.FinanceiroRequestDTO;
import com.belezastudio.api.dto.FinanceiroResponseDTO;
import com.belezastudio.api.model.Financeiro;
import com.belezastudio.api.model.Profissional;
import com.belezastudio.api.repositories.FinanceiroRepository;
import com.belezastudio.api.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinanceiroService {

    @Autowired
    private FinanceiroRepository financeiroRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    // CREATE: Registrar entrada (pagamento) ou saída (despesa).
    public FinanceiroResponseDTO registrarLancamento(FinanceiroRequestDTO dto) {
        if (!dto.tipo().equalsIgnoreCase("ENTRADA") && !dto.tipo().equalsIgnoreCase("SAIDA")) {
            throw new RuntimeException("O tipo deve ser ENTRADA ou SAÍDA.");
        }
        Financeiro financeiro = new Financeiro();
        financeiro.setTipo(dto.tipo().toUpperCase());
        financeiro.setValor(dto.valor());
        financeiro.setFormaPagamento(dto.formaPagamento());
        financeiro.setDescricao(dto.descricao());

        // Se houver um ID de profissional (ex.: pagamento de um serviço feito por ele).
        if (dto.idProfissional() != null) {
            Profissional profissional = profissionalRepository.findById(dto.idProfissional())
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
            financeiro.setProfissional(profissional);
        }

        Financeiro salvo = financeiroRepository.save(financeiro);
        return converterParaDTO(salvo);

    }
    // READ: Listar fechamento de caixa do dia atual
    public List<FinanceiroResponseDTO> listarCaixaDoDia() {
        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDoDia = LocalDate.now().atTime(LocalTime.MAX);

        return financeiroRepository.findByDataLancamentoBetween(inicioDoDia, fimDoDia)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());

    }
    // REGRA DE NEGÓCIO: Calculara comissão de um profissional no mês atual.
    public BigDecimal calcularComissaoMensal(Long idProfissional) {
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado."));

        LocalDateTime inicioDoMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimDoMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);

        // Busca tudo que o profissional ferou de ENTRADA no mês.
        List<Financeiro> servicosRealizados = financeiroRepository
                .findEntradasPorProfissionalEPeriodo(idProfissional, inicioDoMes, fimDoMes);

        // Soma o valor total bruto gerado por ele.
        BigDecimal totalGerado = servicosRealizados.stream()
                .map(Financeiro::getValor)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add((BigDecimal) b));

        // Calcula a comissão baseada na porcentagem cadastrada no perfil dele.
        BigDecimal porcentagem = profissional.getPorcentagemComissao().divide(new BigDecimal("100"));
        return totalGerado.multiply(porcentagem);
    }
    // Método Utilitário.
    private FinanceiroResponseDTO converterParaDTO(Financeiro f) {
        return new FinanceiroResponseDTO(
                f.getIdLancamento(),
                f.getTipo(),
                (BigDecimal) f.getValor(),
                f.getFormaPagamento(),
                f.getDescricao(),
                f.getDataLancamento(),
                f.getProfissional() != null ? f.getProfissional().getIdProfissional() : null,
                f.getProfissional() != null ? f.getProfissional().getUsuario().getNome() : "Despesa Fixa / Salão"
        );
    }
}
