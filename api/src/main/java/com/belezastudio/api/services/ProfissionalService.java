package com.belezastudio.api.services;


import com.belezastudio.api.dto.ProfissionalRequestDTO;
import com.belezastudio.api.dto.ProfissionalResponseDTO;
import com.belezastudio.api.model.Profissional;
import com.belezastudio.api.model.Usuario;
import com.belezastudio.api.repositories.ProfissionalRepository;
import com.belezastudio.api.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Injeção de codificador de senha do Spring Security (BCryptPasswordEncoder) para futura implementação de segurança.
    @Autowired
    private PasswordEncoder passwordEncoder;

    // CREATE
    @Transactional
    public ProfissionalResponseDTO cadastrarProfissional(ProfissionalRequestDTO dto) {

        // 1. CORREÇÃO DA VALIDAÇÃO: Verifica apenas o e-mail, checando se é diferente de nulo
        if (usuarioRepository.findByEmail(dto.email()) != null) {
            throw new RuntimeException("Login ou E-mail já cadastrado no sistema.");
        }
        // 2. Prepara o Usuário.
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setTelefone(dto.telefone());

        // 3. SEGURANÇA: Criptografando a senha do profissional antes de salvar
        usuario.setSenha(passwordEncoder.encode(dto.senha())); // Futuramente adicionar BCrypt.

        usuario.setPerfil("PROFISSIONAL"); // Define o perfil regidamente [cite: 74].
        usuario.setDataNascimento(dto.dataNascimento());

        // 4. Prepara o Profissional.
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

        // CORREÇÃO: Linha adicionada para atualizar a data também no PUT
        usuario.setDataNascimento(dto.dataNascimento());

        // ESTA É A LINHA QUE SALVA O E-MAIL:
        usuario.setEmail(dto.email());

        // IMPORTANTE: Se enviar uma senha nova na atualização, criptografamos também.
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));

        }

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
