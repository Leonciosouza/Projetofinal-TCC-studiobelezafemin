package com.belezastudio.api.services;


import com.belezastudio.api.dto.AgendamentoRequestDTO;
import com.belezastudio.api.dto.AgendamentoResponseDTO;
import com.belezastudio.api.model.Agendamento;
import com.belezastudio.api.model.Profissional;
import com.belezastudio.api.model.Servico;
import com.belezastudio.api.model.Usuario;
import com.belezastudio.api.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    // INJEÇÃO DA NOVA TRAVA DE SEGURANÇA:
    @Autowired
    private BloqueioAgendaRepository bloqueioRepository;

    // CREATE: Cadastrar Agendamento com validação de regras de negócio.
    public AgendamentoResponseDTO cadastrarAgendamento(AgendamentoRequestDTO dto) {

        Usuario cliente = usuarioRepository.findById(dto.idCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        Profissional profissional = profissionalRepository.findById(dto.idProfissional())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado."));

        // 1. Busca a duração do serviço selecionado [cite: 65, 77]
        Servico servico = servicoRepository.findById(dto.idServico())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado."));

        // 2. Calcula a Hora Final com base na duração do serviço.
        LocalTime horaFimCalculada = dto.horaInicio().plusMinutes(servico.getDuracaoMinutos());

        // =============================================================
        // REGRA DE NEGÓCIO 1: VERIFICAÇÃO DE FOLGAS, FÉRIAS E MANUTENÇÃO.
        // Junta a data com a hora para bater com o formato do banco de Bloqueios.
        LocalDateTime inicioAtendimento = dto.dataAtendimento().atTime(dto.horaInicio());
        LocalDateTime fimAtendimento = dto.dataAtendimento().atTime(horaFimCalculada);

        boolean horarioBloqueado = bloqueioRepository.existeBloqueioNoHorario(
                profissional.getIdProfissional(),
                inicioAtendimento,
                fimAtendimento
        );

        if (horarioBloqueado) {
            throw new RuntimeException("Não foi possível agendar: O salão ou o profissional está indisponível neste horário (Folga/Manutenção)");
        }

        //==============================================================


        // REGRA DE NEGÓCIO: Verifica se há choque de horários (Overbooking).
        // 3. Verifica disponibilidade na genda (Regra anti-overbooking) [cite: 67, 68]
        List<Agendamento> conflitos = agendamentoRepository.findConflitosDeHorario(
                profissional.getIdProfissional(), dto.dataAtendimento(), dto.horaInicio(), horaFimCalculada);

        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Horário indisponível! O profissional já possui agendamento neste horário.");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setDataAtendimento(dto.dataAtendimento());
        agendamento.setHoraInicio(dto.horaInicio());
        agendamento.setHoraFim(horaFimCalculada);
        agendamento.setStatus("AGENDADO"); // Status inicial padrão.

        Agendamento salvo = agendamentoRepository.save(agendamento);
        return converterParaDTO(salvo);
    }

    // READ: Listar agendamentos por cliente.
    public List<AgendamentoResponseDTO> listarTodos() {
        return agendamentoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // READ: Buscar agendamentos por cliente(ID).
    public AgendamentoResponseDTO buscarPorID(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));
        return converterParaDTO(agendamento);
    }

    // UPDATE: Atualizar apenas o Status (Boa Prática).
    public AgendamentoResponseDTO atualizarStatus(Long id, String novoStatus) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));

        // ===============================================================================
        // REGRA DE FIDELIDADDE: Credita pontos se o serviço foi concluído.
        // A checagem dupla evita que o cliente ganhe pontos duas vezes pelo mesmo serviço.
        if ("CONCLUIDO".equalsIgnoreCase(novoStatus) && !"CONCLUIDO".equalsIgnoreCase(agendamento.getStatus())){
            Usuario cliente = agendamento.getCliente();

            // Pega os pontos atuais (Se for null no banco, considera 0).
            int pontosAtuais = cliente.getPontosFidelidade() != null ? cliente.getPontosFidelidade() : 0;

            // Adiciona 50 pontos.
            cliente.setPontosFidelidade(pontosAtuais + 50);

            // Slava a pontuação nova no perfil do cliente.
            usuarioRepository.save(cliente);
        }
        // ========================================================================================

        agendamento.setStatus(novoStatus);
        Agendamento atualizado = agendamentoRepository.save(agendamento);
        return converterParaDTO(atualizado);
    }

    // DELETE: Cancelar agendamento fisicamente do banco (Status = "CANCELADO").
    public void deletarAgendamento(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new RuntimeException("Agendamento não encontrado para o ID informado.");
        }
        agendamentoRepository.deleteById(id);
    }

    // MÉTODO UTILITÁRIO PRIVADO PARA CONVERSÃO DE ENTIDADE PARA DTO.
    private AgendamentoResponseDTO converterParaDTO(Agendamento a) {
        return new AgendamentoResponseDTO(
                a.getIdAgendamento(),
                a.getCliente().getNome(),
                a.getProfissional().getUsuario().getNome(),
                a.getServico().getNome(),
                a.getDataAtendimento(),
                a.getHoraInicio(),
                a.getHoraFim(),
                a.getStatus()
        );
    }

}
