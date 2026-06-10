package com.uninter.raizes.controller;

import com.uninter.raizes.model.Usuario;
import com.uninter.raizes.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        Usuario novo = usuarioService.cadastrar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }
}