package com.belezastudio.api.services;

import com.belezastudio.api.model.Agendamento;
import com.belezastudio.api.model.Usuario;
import com.belezastudio.api.repositories.AgendamentoRepository;
import com.belezastudio.api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacaoService {

    // Injetamos o repositório para o bot poder ler a tabela do banco de dados.
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // Injeção do repositório par ao bot poder ler a tabela de clientes.
    @Autowired
    private UsuarioRepository usuarioRepository;

    // ------------------------------------------------------------------------
    // TAREFA 1: LEMBRETES DE AGENDAMENTO (Mantendo sua lógica de HOJE)
    // ------------------------------------------------------------------------

    // Roda TODOS OS DIAS às 08:00 da manhã para evitar que o bot envie notificações duplicadas:
    // @Scheduled(cron = "0 0 8 * * *") // <- Comentando a linha original para funcionalidade de de temporizador de 10 segundos.
    @Scheduled(fixedRate = 10000)       // <- Adicione o temporizador de 10 segundos para testes na App rodando.
    public void dispararLembretesDeAgendamento() {
        System.out.println("[WHATSAPP BOT] Iniciando varredura de agendamentos de hoje (" + LocalDate.now() + ")...");

        // 1. O bot vai no banco e busca apenas a agenda de HOJE, já filtrando só quem está com status "AGENDADO"
        List<Agendamento> agendamentosDeHoje = agendamentoRepository.findByDataAtendimentoAndStatus(LocalDate.now(), "AGENDADO");

        if(agendamentosDeHoje.isEmpty()) {
            System.out.println("[WHATSAPP BOT] Nenhum agendamento pendente para hoje. Nenhuma mensagem enviada.");
            return;
        }

        // 2. O bot varre a lista e simula o disparo individual (o If do status foi removido pois o banco já filtrou).
        for(Agendamento a : agendamentosDeHoje) {
            String nomeCliente = a.getCliente().getNome();
            String telefone = a.getCliente().getTelefone();

            String mensagem = "Olá " + nomeCliente + "! Passando para lembrar do seu horário hoje às " + a.getHoraInicio() + " no Salão Beleza Studio!";

            System.out.println("Enviando WhatsApp para: " + telefone + " -> " + mensagem);
        }

        System.out.println("[WHATSAPP BOT] Todos os lembretes do dia foram processados com sucesso!");
    }

    // ------------------------------------------------------------------------
    // TAREFA 2: MENSAGENS DE ANIVERSÁRIO
    // Roda TODOS OS DIAS às 09:00 da manhã.
    // ------------------------------------------------------------------------

    // @Scheduled(cron = "0 0 9 * * *") <- Comentando a linha original para funcionalidade de de temporizador de 10 segundos.
    @Scheduled(fixedRate = 10000)       // <- Adicione o temporizador de 10 segundos testes na app rodando.
    public void notificarAniversariantes() {
        LocalDate hoje = LocalDate.now();
        System.out.println("[WHATSAPP BOT] Iniciando varredura de aniversariantes do dia (\" + hoje + \")...\"");

        List<Usuario> aniversariantes = usuarioRepository.findAniversariantesDoDia(hoje);

        if (aniversariantes.isEmpty()) {
            System.out.println("[WHATSAPP BOT] Nenhum cliente faz aniversário hoje.");
            return;
        }

        for (Usuario cliente : aniversariantes) {
            String mensagem = "Parabéns, " + cliente.getNome() + "! O Beleza Studio te deseja um feliz aniversário! Venha celebrar com a gente e ganhe um desconto especial.";
            System.out.println("Enviando WhatsApp para: " + cliente.getTelefone() + " -> " + mensagem);
        }
        System.out.println("[WHATSAPP BOT] Todos os aniversariantes do dia foram parabenizados!");

    }
}
