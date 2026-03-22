package com.belezastudio.api.services;


import com.belezastudio.api.dto.ProfissionalRequestDTO;
import com.belezastudio.api.dto.ProfissionalResponseDTO;
import com.belezastudio.api.model.Profissional;
import com.belezastudio.api.model.Usuario;
import com.belezastudio.api.repositories.ProfissionalRepository;
import com.belezastudio.api.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // CREATE
    @Transactional
    public ProfissionalResponseDTO cadastrarProfissional(ProfissionalRequestDTO dto) {
        if (usuarioRepository.findByLogin(dto.login()).isPresent() ||
            usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Login ou E-mail já cadastrado no sistema.");
        }
        // 1. Prepara o Usuário.
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setTelefone(dto.telefone());
        usuario.setLogin(dto.login());
        usuario.setSenha(dto.senha()); // Futuramente adicionar BCrypt.
        usuario.setPerfil("PROFISSIONAL"); // Define o perfil regidamente [cite: 74].
        usuario.setDataNascimento(dto.dataNascimento());

        // 2. Prepara o Profissional.
        Profissional profissional = new Profissional();
        profissional.setUsuario(usuario);
        profissional.setFuncao(dto.funcao());

        // Se a comissão vier nula, define o padrão de 50% conforme documentação.
        profissional.setPorcentagemComissao(dto.porcentagemComissao() != null ? dto.porcentagemComissao() : new java.math.BigDecimal("50.00"));

        // Salva ambos (muito em função do CascadeType.ALL).
        Profissional salvo = profissionalRepository.save(profissional);

        return converterParaDTO(salvo);
    }

    // READ (Todos)
    public List<ProfissionalResponseDTO> listarTodos() {
        return profissionalRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    // READ (Por ID).
    public ProfissionalResponseDTO buscarPorId(Long id) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado."));
        return converterParaDTO(profissional);
    }

    // UPDATE.
    @Transactional
    public ProfissionalResponseDTO atualizarProfissional(Long id, ProfissionalRequestDTO dto) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado."));

        // Atualiza dados do Usuário vinculado.
        Usuario usuario = profissional.getUsuario();
        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        // Não atualizamos o login/email aqui por questão de segurança, precisaria de uma validação extra.

        // Atualiza dados do Profissional.
        profissional.setFuncao(dto.funcao());
        if(dto.porcentagemComissao() != null) {
            profissional.setPorcentagemComissao(dto.porcentagemComissao());
        }
        Profissional atualizado = profissionalRepository.save(profissional);
        return converterParaDTO(atualizado);
    }

    // DELETE.
    @Transactional
    public void deletarProfissional(Long id) {
        if (!profissionalRepository.existsById(id)) {
            throw new RuntimeException("Profissional não encontrado");

        }
        profissionalRepository.deleteById(id);

    }

    // Método Utilitário Privado para conversão.
    private ProfissionalResponseDTO converterParaDTO(Profissional p) {
        return new ProfissionalResponseDTO(
             p.getIdProfissional(),
             p.getUsuario().getIdUsuario(),
             p.getUsuario().getNome(),
             p.getUsuario().getEmail(),
             p.getUsuario().getTelefone(),
             p.getFuncao(),
             p.getPorcentagemComissao()
        );
    }
}
