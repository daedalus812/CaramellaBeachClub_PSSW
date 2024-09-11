package org.ecommerce.caramellabeachclub.security;

import org.ecommerce.caramellabeachclub.services.UtenteDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UtenteDetailsService utenteDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                // Gestione del token JWT scaduto
                logger.warn("JWT token is expired: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Codice 401
                response.getWriter().write("Token scaduto, effettua nuovamente il login.");
                return;  // Interrompi la catena di filtri se il token è scaduto
            } catch (Exception e) {
                // Gestione di eventuali altre eccezioni relative al token
                logger.error("Errore nella validazione del token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Codice 401
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.utenteDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    //se il token è scaduto, si intercetta l'eccezione ExpiredJwtException, si registra un messaggio di avvertimento
    // e si invia una risposta HTTP con stato 401,
    // interrompendo la catena di filtri. Il messaggio inviato nella risposta informa l'utente che il token è scaduto.

}
