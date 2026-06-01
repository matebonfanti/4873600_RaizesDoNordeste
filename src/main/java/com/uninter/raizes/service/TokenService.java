package com.uninter.raizes.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.uninter.raizes.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    
    @Value("${api.security.token.secret:CHAVE_SECRETA_4873600}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        Algorithm algoritmo = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("Raizes") 
                .withSubject(usuario.getEmail())
                .withClaim("tipoUsuario", usuario.getTipoUsuario().name())
                .withExpiresAt(gerarDataExpiracao()) 
                .sign(algoritmo); 
    }

 

    public String getSubject(String tokenJWT) {
    try {
        Algorithm algoritmo = Algorithm.HMAC256(secret);
        return JWT.require(algoritmo).withIssuer("Raizes")
                .build().verify(tokenJWT).getSubject(); 
    } catch (JWTVerificationException exception) {
        throw new IllegalArgumentException("Token JWT inválido ou expirado!");
    }
}


   //token expira em 2 horas
    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}