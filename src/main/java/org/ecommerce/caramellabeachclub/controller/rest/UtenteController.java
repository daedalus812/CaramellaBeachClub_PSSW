package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/utenti")
public class UtenteController {


    @Autowired
    private UtenteService utenteService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        try {
            boolean credenzialiValide = utenteService.verificaCredenziali(email, password);

            if (credenzialiValide) {
                return new ResponseEntity<>("Login avvenuto con successo!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Credenziali non valide!", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante il login.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}