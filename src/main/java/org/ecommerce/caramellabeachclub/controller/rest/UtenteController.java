package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/utenti")
public class UtenteController {

    @Autowired
    private UtenteRepository utenteRepository;

    @GetMapping("/me")
    public ResponseEntity<Utente> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        Utente currentUser = utenteRepository.findByEmail(email);
        if (currentUser != null) {
            return ResponseEntity.ok(currentUser);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/me")
    public ResponseEntity<Utente> updateCurrentUser(@RequestBody Utente updatedUser, Authentication authentication) {
        String email = authentication.getName();
        Utente currentUser = utenteRepository.findByEmail(email);
        if (currentUser != null) {
            // Aggiorna solo il campo telefono per sicurezza
            currentUser.setTelefono(updatedUser.getTelefono());
            utenteRepository.save(currentUser);
            return ResponseEntity.ok(currentUser);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}