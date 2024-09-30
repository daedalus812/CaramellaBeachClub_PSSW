package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.services.RecensioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recensioni")
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;

    // Aggiungi una recensione
    @PostMapping("/aggiungi")
    public ResponseEntity<String> aggiungiRecensione(
            @RequestParam int utente,
            @RequestParam int ordine,
            @RequestParam int prodotto,
            @RequestParam String commento,
            @RequestParam String valutazione) {
        try {
            recensioneService.aggiungiRecensione(utente, ordine, prodotto, commento, valutazione);
            return ResponseEntity.ok("Recensione aggiunta con successo");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Rimuovi una recensione
    @DeleteMapping("/elimina")
    public ResponseEntity<String> eliminaRecensione(
            @RequestParam int idRecensione,
            @RequestParam int utenteId) {
        try {
            recensioneService.eliminaRecensione(idRecensione, utenteId);
            return ResponseEntity.ok("Recensione eliminata con successo");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}