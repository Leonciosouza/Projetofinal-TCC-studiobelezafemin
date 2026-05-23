package com.belezastudio.api.services;

import com.belezastudio.api.dto.ClienteRequestDTO;
import com.belezastudio.api.dto.ClienteResponseDTO;
import com.belezastudio.api.model.Usuario;
import com.belezastudio.api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    private Object usuario;

    public ClienteResponseDTO cadastrarCliente(ClienteRequestDTO dto) {
        // Valida se o Login ou email já existem.
        if (usuarioRepository.findByLogin(dto.login()).isPresent() ||
            usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Login ou E-mail já estão em uso.");

        }
        // Converte DTO para Model (Entidade).
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setTelefone(dto.telefone());
        usuario.setLogin(dto.login());

        // Dica: Futuramente, integraremos o BCryptPasswordEncoder do Spring Security aqui.
        usuario.setSenha(dto.senha());

        usuario.setPerfil("CLIENTE"); // Define automaticamente o perfil.
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setPontosFidelidade(0);

        // Salva no banco.
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Retorna a resposta sem a senha.
        return new ClienteResponseDTO(
                usuarioSalvo.getIdUsuario(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getTelefone(),
                usuarioSalvo.getPontosFidelidade(),
                usuario.getDataNascimento());
    }
    // READ (Todos): Listar todos os clientes.
    public List<ClienteResponseDTO> listarTodosClientes() {
        // Busca todos os usuários, filtra apenas os que são CLIENTES, e converte para DTO
        return usuarioRepository.findAll().stream()
                .filter(usuario -> "CLIENTE".equals(usuario.getPerfil()))
                .map(this::converterParaDTO) // <-- A mágica da conversão limpa acontece aqui
                .collect(Collectors.toList());
    }

    // READ (Por ID) - A busca por ID é permitida, mas a resposta não inclui a senha.
    public ClienteResponseDTO buscarPorId(Long id) {
        // Busca o usuário pelo ID no PostgreSQL ou lança uma exceção customizada
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o ID informado."));

        // Transforma a Entidade do banco no DTO de resposta seguro
        return new ClienteResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPontosFidelidade(),
                usuario.getDataNascimento()
        );
    }

    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        // Atualiza apenas os campos permitidos
        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setEmail(dto.email());

        // Salva as alterações no banco de dados
        Usuario atualizado = usuarioRepository.save(usuario);

        // Retorna o DTO atualizado
        return new ClienteResponseDTO(
                atualizado.getIdUsuario(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getTelefone(),
                atualizado.getPontosFidelidade(),
                usuario.getDataNascimento());
    }
    // DELETE - A exclusão física é permitida, mas apenas se o cliente existir.
    public void deletarCliente(Long id) {
        // Verifica se o ID existe na tabela de usuários
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado para o ID informado.");
        }
        // Se existir, efetua a exclusão física no PostgreSQL
        usuarioRepository.deleteById(id);
    }

    // Método Utilitário Privado para converter a Entidade do banco no DTO de resposta seguro.
    private ClienteResponseDTO converterParaDTO(Usuario usuario) {
        // Certifique-se de que a ordem dos campos aqui bate com o construtor do seu ClienteResponseDTO
        return new ClienteResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPontosFidelidade(),
                usuario.getDataNascimento()
                // Obs: Se o seu DTO agora exige a Data de Nascimento no construtor,
                // basta adicionar a linha: usuario.getDataNascimento(),
        );
    }
}
