package com.uninter.raizes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;



@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private SecurityFilter securityFilter;

   @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(req -> {

                req.requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/webjars/**",
                        "/error"
                ).permitAll();

                req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                req.requestMatchers(HttpMethod.POST, "/usuarios").permitAll();

                req.requestMatchers("/estoque/**").hasAuthority("GERENTE");
                req.requestMatchers(HttpMethod.POST, "/produtos").hasAuthority("GERENTE");
                req.requestMatchers(HttpMethod.PATCH, "/produtos/**").hasAuthority("GERENTE");
                req.requestMatchers(HttpMethod.DELETE, "/produtos/**").hasAuthority("GERENTE");
                req.requestMatchers(HttpMethod.POST, "/unidades").hasAuthority("GERENTE");
                req.requestMatchers(HttpMethod.PATCH, "/unidades/**").hasAuthority("GERENTE");

                req.anyRequest().authenticated();
            })
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        
        return new BCryptPasswordEncoder();
    }
}


//Futura Melhoria: Implementar restrição de rotas de unidade de produtos para somente 
//o GETENTE poder alterar

//10/06 - Ajustes de permssão para somente o gerente alterar produtos, unidades e estoque.
