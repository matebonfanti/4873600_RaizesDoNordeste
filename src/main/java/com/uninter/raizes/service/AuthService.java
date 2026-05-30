package com.uninter.raizes.service;

import com.uninter.raizes.dto.LoginDTO;
import com.uninter.raizes.model.Usuario;
import com.uninter.raizes.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public String autenticar(LoginDTO dadosLogin) {

        Optional<Usuario> usuarioAux = usuarioRepository.findByEmail(dadosLogin.getEmail());
        if (usuarioAux.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioAux.get();

        boolean senhaValida = passwordEncoder.matches(dadosLogin.getSenha(), usuario.getSenha());
        if (!senhaValida) {
            throw new RuntimeException("Senha inválida");
        }

        return tokenService.gerarToken(usuario);

}

}
