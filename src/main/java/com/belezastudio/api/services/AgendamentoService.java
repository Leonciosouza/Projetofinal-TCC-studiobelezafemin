package com.belezastudio.api.services;


import com.belezastudio.api.model.Agendamento;
import com.belezastudio.api.model.Servico;
import com.belezastudio.api.repositories.AgendamentoRepository;
import com.belezastudio.api.repositories.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    public Agendamento criarAgendamento(Agendamento agendamento) {
        // 1. Busca a duração do serviço selecionado [cite: 65, 77]
        Servico servico = servicoRepository.findById(agendamento.getServico().getIdServico())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado."));

        // 2. Calcula a Hora Fim automaticamente.
        LocalTime horaCalculadaFim = agendamento.getHoraInicio().plusMinutes(servico.getDuracaoMinutos());
        agendamento.setHoraFim(horaCalculadaFim);

        // 3. Verifica disponibilidade na genda (Regra anti-overbooking) [cite: 67, 68]
        List<Agendamento> conflitos = agendamentoRepository.findConflitosDeHorario(
                agendamento.getProfissional().getIdProfissional(),
                agendamento.getDataAtendimento(),
                agendamento.getHoraInicio(),
                agendamento.getHoraFim()
        );

        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Horário indisponível para este profissional.");
        }
        return agendamentoRepository.save(agendamento);
    }
}
