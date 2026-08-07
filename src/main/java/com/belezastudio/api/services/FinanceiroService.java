package com.belezastudio.api.services;


import com.belezastudio.api.dto.*;
import com.belezastudio.api.model.Agendamento;
import com.belezastudio.api.model.Financeiro;
import com.belezastudio.api.model.Profissional;
import com.belezastudio.api.repositories.AgendamentoRepository;
import com.belezastudio.api.repositories.FinanceiroRepository;
import com.belezastudio.api.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Locale;

@Service
public class FinanceiroService {

    @Autowired
    private FinanceiroRepository financeiroRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ClienteService clienteService;


    // CREATE: Registrar entrada (pagamento) ou saída (despesa).
    public FinanceiroResponseDTO registrarLancamento(FinanceiroRequestDTO dto) {

        // Validação da Restrição CHECK do Banco de dados.
        if (!dto.tipo().equals("ENTRADA") && !dto.tipo().equals("SAIDA")) {
            throw new RuntimeException("O tipo deve ser exclusivamente 'ENTRADA' ou 'SAIDA'.");
        }

        Financeiro financeiro = new Financeiro();
        financeiro.setTipo(dto.tipo());
        financeiro.setValor(dto.valor());
        financeiro.setFormaPagamento(dto.formaPagamento());
        financeiro.setDescricao(dto.descricao());

        // Se houver um ID de profissional (ex.: pagamento de um serviço feito por ele).
        // Se veio um ID de profissional, nós vinculamos. Se não, deixamos nulo (Despesa do salão).
        if (dto.idProfissional() != null) {
            Profissional prof = profissionalRepository.findById(dto.idProfissional())
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
            financeiro.setProfissional(prof);
        }

        // ALTERAÇÃO RECENTE: Vinculação condicional do Agendamento que originou a receita.
        if (dto.idAgendamento() != null) {
            Agendamento agend = agendamentoRepository.findById(dto.idAgendamento())
                    .orElseThrow(() -> new RuntimeException("Agendamento não encontrado para o ID informado."));
            financeiro.setAgendamento(agend);
        }

        Financeiro salvo = financeiroRepository.save(financeiro);

        // =========================================================================
        // 2. INTEGRAÇÃO: SISTEMA DE FIDELIDADE (Módulo Cliente)
        // =========================================================================
        // Se entrou dinheiro no caixa e esse dinheiro veio de um agendamento...
        if (salvo.getTipo().equals("ENTRADA") && salvo.getAgendamento() != null) {

            // Navega pelos relacionamentos para descobrir quem é o dono do agendamento.
            Long idCliente = salvo.getAgendamento().getCliente().getIdUsuario();
            BigDecimal valorDaTransacao = salvo.getValor();

            // Aciona o CleinteService para creditar os pontos na conta dessa cleinte!
            clienteService.adicionarPontosFidelidade(idCliente, valorDaTransacao);

        }
        //==============================================================================

        // 3. Devolve a resposta padronizada para o Postman (ou qualquer outro cliente REST).
        return converterParaDTO(salvo);

    }
    //  CRUD - READ: Listagem geral para auditoria todo o fluxo de caixa.
    public List<FinanceiroResponseDTO> listarTodos() {
        return financeiroRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // CRUD - DELETE: Apagar um lançamento errado para registros.
    public void deletarLancamento(Long id) {
        if (!financeiroRepository.existsById(id)) {
            throw new RuntimeException("Registro financeiro não encontrado.");
        }
        financeiroRepository.deleteById(id);
    }

    // NOVA REGRA DE NEGÓCIO: Calcular Comissão Mensal (Dias Úteis)
    public ComissaoResponseDTO calcularComissaoMensal(Long idProfissional, int mes, int ano ) {

        Profissional prof = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        // Definie o primeiro e o último milissegundo do mês solicitado.
        YearMonth anoMes = YearMonth.of(ano, mes);
        LocalDateTime inicioMes = anoMes.atDay(1).atStartOfDay();
        LocalDateTime fimMes = anoMes.atEndOfMonth().atTime(23, 59, 59);

        // Busca todas as entradas de dinheiro geradas por este profissional no mês.
        List<Financeiro> entradasDoMes = financeiroRepository
                .buscarEntradasPorProfissionalEPeriodo(idProfissional, inicioMes, fimMes);

        BigDecimal acumuladorFaturamentoValido = BigDecimal.ZERO;

        // Filtra: Soma o valor APENAS se o dia for válido (Não for Domingo nem Feriado).
        for (Financeiro lancamento: entradasDoMes) {
            LocalDate dataDoFaturamento = lancamento.getDataLancamento().toLocalDate();

            if (isDiaValidoParaComissao(dataDoFaturamento)) {
                acumuladorFaturamentoValido = acumuladorFaturamentoValido.add(lancamento.getValor());
            }

        }
        // Calcula a comissão final: (Total Válido * Porcentagem) / 100.
        BigDecimal taxaComissao = prof.getPorcentagemComissao();
        BigDecimal valorLiquidoAReceber = acumuladorFaturamentoValido
                .multiply(taxaComissao)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

        return new ComissaoResponseDTO(
                prof.getIdProfissional(),
                prof.getUsuario().getNome(),
                mes,
                ano,
                acumuladorFaturamentoValido,
                taxaComissao,
                valorLiquidoAReceber
        );

    };

    // NOVO RECURSO GERENCIAL: Balanço Mensal de Lucros e Prejuízos (Fluxo de Caixa) - Para o Proprietário do Salão.
    public BalancoMensalResponseDTO gerarBalancoMensal(int mes, int ano) {
        YearMonth anoMesTarget = YearMonth.of(ano, mes);
        LocalDateTime inicioMes = anoMesTarget.atDay(1).atStartOfDay();
        LocalDateTime fimMes = anoMesTarget.atEndOfMonth().atTime(23, 59, 59);

        // Busca tudo o que aconteceu no salão neste mês.
        List<Financeiro> todosLancamentos = financeiroRepository
                .buscarLancamentosPorPeriodo(inicioMes, fimMes);

        BigDecimal totalEntradas = BigDecimal.ZERO;
        BigDecimal totalSaidas = BigDecimal.ZERO;

        for (Financeiro f : todosLancamentos) {
            if (f.getTipo().equals("ENTRADA")) {
                totalEntradas = totalEntradas.add(f.getValor());
            } else if (f.getTipo().equals("SAIDA")) {
                totalSaidas = totalSaidas.add(f.getValor());
            }
        }

        BigDecimal saldoLiquido = totalEntradas.subtract(totalSaidas);
        String situacao = saldoLiquido.compareTo(BigDecimal.ZERO) >= 0 ? "LUCRO" : "PREJUIZO";

        return new BalancoMensalResponseDTO(mes, ano, totalEntradas, totalSaidas, saldoLiquido, situacao);

    }
    // NOVO RECURSO GERENCIAL 2: Fechamento de Caixa Diário Automático.
    public FechamentoDiarioResponseDTO gerarFechamentoDiario(LocalDate data) {
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.atTime(23, 59, 59);

        List<Financeiro> lancamentosDoDia = financeiroRepository
                .buscarLancamentosPorPeriodo(inicioDia, fimDia);

        BigDecimal faturamentoBrutoDia = BigDecimal.ZERO;
        Map<Profissional, BigDecimal> producaoPorProfissional = new HashMap<>();

        // Varrer apenas as ENTRADAS do dia.
        for (Financeiro f: lancamentosDoDia) {
            if (f.getTipo().equals("ENTRADA")) {
                faturamentoBrutoDia = faturamentoBrutoDia.add(f.getValor());

                // Se houver profissional atrelado, soma no "cofrinho" dele.
                if (f.getProfissional() != null) {
                    Profissional prof = f.getProfissional();
                    BigDecimal totalAtual = producaoPorProfissional.getOrDefault(prof, BigDecimal.ZERO);
                    producaoPorProfissional.put(prof, totalAtual.add(f.getValor()));

                }
            }
        }

        // Aplicar a Regra de Negócio: Comissão é zero se for Domingo ou Feriado!
        boolean diaGeraComissao = isDiaValidoParaComissao(data);

        BigDecimal totalComissoesDevidas = BigDecimal.ZERO;
        List<ComissaoDiariaProfissionalDTO> detalhamento = new ArrayList<>();

        // Instancia o formatador nativo do Java para a moeda Real Brasileira (R$)
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        for (Map.Entry<Profissional, BigDecimal> entry : producaoPorProfissional.entrySet()) {
            Profissional prof = entry.getKey();
            BigDecimal produzido = entry.getValue();
            BigDecimal comissao = BigDecimal.ZERO;

            if (diaGeraComissao) {
                comissao = produzido.multiply(prof.getPorcentagemComissao()).
                        divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            }

            totalComissoesDevidas = totalComissoesDevidas.add(comissao);

            // Converte os valores em texto já no formato "R$ 0,00" para o Postman.
            detalhamento.add(new ComissaoDiariaProfissionalDTO(
                    prof.getIdProfissional(),
                    prof.getUsuario().getNome(),
                    formatadorMoeda.format(produzido), // Valor Produzido.
                    formatadorMoeda.format(comissao)   // Comissão Devida.
            ));
        }

        BigDecimal saldoRetidoSalao = faturamentoBrutoDia.subtract(totalComissoesDevidas);

        return new FechamentoDiarioResponseDTO(
                data,
                formatadorMoeda.format(faturamentoBrutoDia),
                formatadorMoeda.format(totalComissoesDevidas),
                formatadorMoeda.format(saldoRetidoSalao),
                detalhamento
        );
    }

    // METODO AUXILIAR DA REGRA DE NEGÓCIO: Avaliação cronológica rigorosa.
    private boolean isDiaValidoParaComissao(LocalDate data) {
        // 1. Desconsiderar Domingos do fluxo de produção passível de comissão.
        if (data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }

        // 2. Desconsiderar Feriados Fixos Nacionais
        List<MonthDay> feriadosNacionais = Arrays.asList(
                MonthDay.of(1, 1),   // Confraternização Universal
                MonthDay.of(4, 21),  // Tiradentes
                MonthDay.of(5, 1),   // Dia do Trabalhador
                MonthDay.of(9, 7),   // Independência do Brasil
                MonthDay.of(10, 12), // Nossa Sra. Aparecida
                MonthDay.of(11, 2),  // Finados
                MonthDay.of(11, 15), // Proclamação da República
                MonthDay.of(12, 25)  // Natal
        );

        MonthDay diaMesAtual = MonthDay.from(data);
        if (feriadosNacionais.contains(diaMesAtual)) {
            return false; // É feriado, então retorna falso, ignora a produção do dia.
        }

        // Se passou pelos testes, é dia útil para comissão!
        return true;
    }

    // Método Utilitário.
    private FinanceiroResponseDTO converterParaDTO(Financeiro f) {
        Long idProf = (f.getProfissional() != null) ? f.getProfissional().getIdProfissional() : null;
        String nomeProf = (f.getProfissional() != null) ? f.getProfissional().getUsuario().getNome() : null;
        Long idAgend = (f.getAgendamento() != null) ? f.getAgendamento().getIdAgendamento() : null;

        return new FinanceiroResponseDTO(
                f.getIdLancamento(),
                f.getTipo(),
                f.getValor(), // Removido o cast (BigDecimal) desnecessário
                f.getFormaPagamento(),
                f.getDescricao(),
                f.getDataLancamento(),
                idProf,
                nomeProf,
                idAgend // Enviado aqui.
        );
    }
}
