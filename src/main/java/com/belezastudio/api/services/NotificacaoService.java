package com.belezastudio.api.services;

import com.belezastudio.api.model.Agendamento;
import com.belezastudio.api.repositories.AgendamentoRepository;
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

    // Roda TODOS OS DIAS às 08:00 da manhã para evitar que o bot envie notificações duplicadas.
    @Scheduled(cron = "0 0 8 * * *")
    public void dispararLembretesDeAgendamento() {
        System.out.println("[WHATSAPP BOT] Iniciando varredura de agendamentos de hoje (" + LocalDate.now() + ")...");

        // 1. O bot vai no banco e busca apenas a agenda de HOJE
        List<Agendamento> agendamentosDeHoje = agendamentoRepository.findByDataAtendimento(LocalDate.now());

        if(agendamentosDeHoje.isEmpty()) {
            System.out.println("[WHATSAPP BOT] Nenhum agendamento para hoje. Nenhuma mensagem enviada.");
            return;
        }

        // 2. O bot varre a lista e simula o disparo individual
        for(Agendamento a : agendamentosDeHoje) {

            // Só manda lembrete se o status ainda for "AGENDADO".
            if(a.getStatus().equalsIgnoreCase("AGENDADO")) {
                String nomeCliente = a.getCliente().getNome();
                String telefone = a.getCliente().getTelefone();

                String mensagem = "Olá " + nomeCliente + "! Passando para lembrar do seu horário hoje às " + a.getHoraInicio() + " no Salão Beleza Studio!";

                // Na vida real, aqui entraria o código da API do WhatsApp para disparar a mensagem. Por enquanto, só vamos simular.
                // Para o TCC, o print no console já prova que a arquitetura está funcionando.
                System.out.println("Enviando WhatsApp para: " + telefone + " -> " + mensagem);
            }
        }
        System.out.println("[WHATSAPP BOT] Todos os lembretes do dia foram processados com sucesso!");
    }
}
