package com.uninter.raizes.service;

import com.uninter.raizes.model.Usuario;
import com.uninter.raizes.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        
    }

   public Usuario cadastrar(Usuario usuario){
    if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
        throw new IllegalArgumentException("Email já cadastrado.");
    }
    String senhaCodificada = passwordEncoder.encode(usuario.getSenha());
    usuario.setSenha(senhaCodificada);
    return usuarioRepository.save(usuario);
}
}