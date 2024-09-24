package org.ecommerce.caramellabeachclub.controller.rest;

import lombok.Getter;
import lombok.Setter;
import org.ecommerce.caramellabeachclub.entities.Recensione;
import org.ecommerce.caramellabeachclub.services.RecensioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recensioni")
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;

    // Aggiungi una nuova recensione
    @PostMapping("/aggiungi")
    public ResponseEntity<String> aggiungiRecensione(@RequestBody RecensioneRequest request) {
        try {
            recensioneService.saveRecensione(
                    request.getProdottoId(),
                    request.getUtenteId(),
                    request.getValutazione(),
                    request.getCommento()
            );
            return ResponseEntity.ok("Recensione aggiunta con successo.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Recupera tutte le recensioni per un determinato utente
    @GetMapping("/utente/{idUtente}")
    public ResponseEntity<List<Recensione>> getRecensioniByUtente(@PathVariable int idUtente) {
        try {
            List<Recensione> recensioni = recensioneService.getRecensioniByUtente(idUtente);
            if (recensioni.isEmpty()) {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.ok(recensioni);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Recupera tutte le recensioni per un determinato prodotto
    @GetMapping("/prodotto/{idProdotto}")
    public ResponseEntity<List<Recensione>> getRecensioniByProdotto(@PathVariable int idProdotto) {
        try {
            List<Recensione> recensioni = recensioneService.getRecensioniByProdotto(idProdotto);
            if (recensioni.isEmpty()) {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.ok(recensioni);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null);
        }
    }



    // Classe interna per gestire i parametri della richiesta
    @Setter
    @Getter
    public static class RecensioneRequest {
        private Integer utenteId;
        private Integer prodottoId;
        private String valutazione;
        private String commento;
    }
}
