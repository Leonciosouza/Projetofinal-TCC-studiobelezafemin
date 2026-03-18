package com.belezastudio.api.services;


import com.belezastudio.api.dto.ServicoRequestDTO;
import com.belezastudio.api.dto.ServicoResponseDTO;
import com.belezastudio.api.model.Servico;
import com.belezastudio.api.repositories.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ProfissionalRespository profissionalRespository;

    // CREATE.
    public  ServicoResponseDTO cadastrarServico(ServicoRequestDTO dto) {
        // Busca o profissional no banco para garantir que ele existe.
        Profissional profissional = profissionalRespository.findById(dto.idProfissional())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado para o ID informado."));

        Servico servico = new Servico();
        servico setNome(dto.nome());
        servico.setDescricao(dto.descricao());
        servico.setDuracaoMinutos(dto.duracaoMinutos());
        servico.setPrecoPadrao(dto.precoPadrao());
        servico.setProfissional(profissional); // Realiza o vínculo.

        Servico salvo = servicoRepository.save(servico);
        return converterParaDTO(salvo);

    }

    // READ (Todos).
    public List<ServicoResponseDTO> listarTodos() {
        return servicoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // READ (Por profissional - Essencial para o fluxo do Cliente)
    public List<ServicoResponseDTO> listarPorProfisssional(Long idProfissional) {
        return  servicoRepository.findByProfissionalIdProfissional(idProfissional).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // UPDATE.
    public ServicoResponseDTO atualizarServico(Long id, ServicoRequestDTO dto) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado."));

        // Se o ID do profissional mudar, precisamos buscar o novo profissional.
        if(!servico.getProfissional().getIdProfissional().equals(dto.idProfissional())) {
            Profissional novoProfissional = profissionalRespository.findById(dto.idProfissional())
                    .orElseThrow(() -> new RuntimeException("Novo Profissional não encontrado."));
            servico.setProfissional(novoProfissional);
        }

        servico.setNome(dto.nome());
        servico.setDescricao(dto.descricao());
        servico.setDuracaoMinutos(dto.duracaoMinutos());
        servico.setPrecoPadrao(dto.precoPadrao());

        Servico atualizado = servicoRepository.save(servico);
        return converterParaDTO(atualizado);
    }

    // DELETE.
    public void deletarServico(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new RuntimeException("Serviço não encontrado.");
        }
        servicoRepository.deleteById(id);
    }

    // MÉTODO UTILITÁRIO.
    private ServicoResponseDTO converterParaDTO(Servico s) {
        return new ServicoResponseDTO(
             s.setIdServico(),
             s.setNome(),
             s.getDescricao(),
             s.getDuracaoMinutos(),
             s.getPrecoPadrao(),
             s.getProfissional().getIdProfissional(),
             s.getProfissional().getUsuario().getNome()
        );

    }

}
