package org.ecommerce.caramellabeachclub.config;

import org.ecommerce.caramellabeachclub.security.JwtAuthenticationFilter;
import org.ecommerce.caramellabeachclub.services.UtenteDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UtenteDetailsService utenteDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UtenteDetailsService utenteDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.utenteDetailsService = utenteDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Usa Customizer per disabilitare CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/registrazione").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Aggiungi il filtro JWT prima di UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

