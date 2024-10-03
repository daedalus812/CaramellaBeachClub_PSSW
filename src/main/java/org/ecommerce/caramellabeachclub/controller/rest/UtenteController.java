package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.ecommerce.caramellabeachclub.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/utenti")
public class UtenteController {
    private final UtenteService userService;
    private final UtenteRepository utenteRepository;


    public UtenteController(UtenteService userService, UtenteRepository utenteRepository) {
        this.userService = userService;
        this.utenteRepository = utenteRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<Utente> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Utente currentUser = (Utente) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

}
