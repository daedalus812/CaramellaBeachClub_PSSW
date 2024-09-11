package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.models.AuthenticationResponse;
import org.ecommerce.caramellabeachclub.security.JwtUtil;
import org.ecommerce.caramellabeachclub.services.UtenteDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/utenti")
public class UtenteController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UtenteDetailsService utenteDetailsService;

    // Endpoint per il login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenziali non valide");
        }

        final UserDetails userDetails = utenteDetailsService.loadUserByUsername(email);
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    // Altri endpoint...
}
