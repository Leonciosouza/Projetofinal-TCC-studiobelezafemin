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

        usuario.setPerfil("CLEINTE"); // Define automaticamente o perfil.
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
                usuarioSalvo.getPontosFidelidade()
        );
    }

    public List<ClienteResponseDTO> listarTodosClientes() {
        List<Usuario> clientes = usuarioRepository.findByPerfil("CLIENTE");

        return clientes.stream().map(c -> new ClienteResponseDTO(
               c.getIdUsuario(), c.getNome(), c.getEmail(), c.getTelefone(), c.getPontosFidelidade()
        )).collect(Collectors.toList());
    }

}
