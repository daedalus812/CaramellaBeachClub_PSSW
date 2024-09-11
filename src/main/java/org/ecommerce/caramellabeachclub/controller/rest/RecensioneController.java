package org.ecommerce.caramellabeachclub.controllers;

import org.ecommerce.caramellabeachclub.entities.Recensione;
import org.ecommerce.caramellabeachclub.entities.Ordine;
import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.services.RecensioneService;
import org.ecommerce.caramellabeachclub.repositories.RecensioneRepository;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidOperationException;
import org.ecommerce.caramellabeachclub.resources.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recensioni")
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;

    @Autowired
    private RecensioneRepository recensioneRepository;

    // Aggiungi una nuova recensione
    @PostMapping("/aggiungi")
    public ResponseEntity<String> aggiungiRecensione(@RequestBody RecensioneRequest request) {
        try {
            recensioneService.aggiungiRecensione(request.getUtente(), request.getOrdine(),
                    request.getProdotto(), request.getValutazione(), request.getCommento());
            return ResponseEntity.ok("Recensione aggiunta con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("Utente non trovato.");
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Parametri di input non validi.");
        }
    }

    // Recupera tutte le recensioni per un determinato utente
    @GetMapping("/utente/{idUtente}")
    public ResponseEntity<List<Recensione>> getRecensioniByUtente(@PathVariable int idUtente) {
        Utente utente = new Utente();
        utente.setId(idUtente);
        List<Recensione> recensioni = recensioneRepository.findAllByUtente(utente);
        if (recensioni.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(recensioni);
    }

    // Recupera tutte le recensioni presenti nel database
    @GetMapping("/tutte")
    public ResponseEntity<List<Recensione>> getAllRecensioni() {
        List<Recensione> recensioni = recensioneRepository.findAll();
        return ResponseEntity.ok(recensioni);
    }

    // Cancella una recensione per ID
    @DeleteMapping("/cancella/{id}")
    public ResponseEntity<String> cancellaRecensione(@PathVariable int id) {
        if (recensioneRepository.existsById(id)) {
            recensioneRepository.deleteById(id);
            return ResponseEntity.ok("Recensione cancellata con successo.");
        } else {
            return ResponseEntity.status(404).body("Recensione non trovata.");
        }
    }

    // Aggiorna una recensione esistente
    @PutMapping("/aggiorna/{id}")
    public ResponseEntity<String> aggiornaRecensione(@PathVariable int id, @RequestBody RecensioneRequest request) {
        return recensioneRepository.findById(id).map(recensione -> {
            recensione.setValutazione(request.getValutazione());
            recensione.setCommento(request.getCommento());
            recensioneRepository.save(recensione);
            return ResponseEntity.ok("Recensione aggiornata con successo.");
        }).orElse(ResponseEntity.status(404).body("Recensione non trovata."));
    }

    // Classe interna per gestire i parametri della richiesta
    public static class RecensioneRequest {
        private Utente utente;
        private Ordine ordine;
        private Prodotto prodotto;
        private String valutazione;
        private String commento;

        // Getters e Setters

        public Utente getUtente() {
            return utente;
        }

        public void setUtente(Utente utente) {
            this.utente = utente;
        }

        public Ordine getOrdine() {
            return ordine;
        }

        public void setOrdine(Ordine ordine) {
            this.ordine = ordine;
        }

        public Prodotto getProdotto() {
            return prodotto;
        }

        public void setProdotto(Prodotto prodotto) {
            this.prodotto = prodotto;
        }

        public String getValutazione() {
            return valutazione;
        }

        public void setValutazione(String valutazione) {
            this.valutazione = valutazione;
        }

        public String getCommento() {
            return commento;
        }

        public void setCommento(String commento) {
            this.commento = commento;
        }
    }
}
