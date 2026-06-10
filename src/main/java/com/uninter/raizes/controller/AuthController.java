package com.uninter.raizes.controller;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import com.uninter.raizes.dto.LoginDTO;
import com.uninter.raizes.service.AuthService;
import com.uninter.raizes.dto.TokenDTO;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }



    @PostMapping("/login")
    public ResponseEntity<TokenDTO> fazerLogin(@RequestBody LoginDTO dadosLogin) {
        
        String token = authService.autenticar(dadosLogin);
        
       
        return ResponseEntity.ok(new TokenDTO(token));
    }
    
}
