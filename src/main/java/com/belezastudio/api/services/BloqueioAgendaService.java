package com.belezastudio.api.services;


import com.belezastudio.api.dto.BloqueioAgendaRequestDTO;
import com.belezastudio.api.dto.BloqueioAgendaResponseDTO;
import com.belezastudio.api.model.BloqueioAgenda;
import com.belezastudio.api.model.Profissional;
import com.belezastudio.api.repositories.BloqueioAgendaRepository;
import com.belezastudio.api.repositories.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BloqueioAgendaService {

    @Autowired
    private BloqueioAgendaRepository bloqueioRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    public BloqueioAgendaResponseDTO criarBloqueio(BloqueioAgendaRequestDTO dto) {
        if (dto.dataHoraFim().isBefore(dto.dataHoraInicio()) || dto.dataHoraFim().isEqual(dto.dataHoraInicio())) {
            throw new RuntimeException("A data de fim deve ser posterior à data de início.");
        }

        BloqueioAgenda bloqueio = new BloqueioAgenda();
        bloqueio.setDataHoraInicio(dto.dataHoraInicio());
        bloqueio.setDataHoraFim(dto.dataHoraFim());
        bloqueio.setMotivo(dto.motivo());

        // Associa ao profissional (Se o ID for enviado).
        if (dto.idProfissional() != null) {
            Profissional profissional = profissionalRepository.findById(dto.idProfissional())
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado."));
            bloqueio.setProfissional(profissional);

        }

        BloqueioAgenda salvo = bloqueioRepository.save(bloqueio);

        Long idProf = salvo.getProfissional() != null ? salvo.getProfissional().getIdProfissional() : null;
        return new BloqueioAgendaResponseDTO(salvo.getIdBloqueio(), idProf, salvo.getDataHoraInicio(), salvo.getDataHoraFim(), salvo.getMotivo());
    }

}
