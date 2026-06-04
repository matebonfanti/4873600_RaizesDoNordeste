package com.uninter.raizes.config;

import com.uninter.raizes.repository.UsuarioRepository;
import com.uninter.raizes.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        
        String tokenJWT = recuperarToken(request);
        
        if (tokenJWT != null) {
            String email = tokenService.getSubject(tokenJWT);
            var usuarioOptional = usuarioRepository.findByEmail(email);
            
            if (usuarioOptional.isPresent()) {
                var usuario = usuarioOptional.get();
                var authority = new SimpleGrantedAuthority(usuario.getTipoUsuario().name());
                var authentication = new UsernamePasswordAuthenticationToken(
                        usuario, 
                        null, 
                        Collections.singletonList(authority)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}