package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.entities.CarrelloProdotto;
import org.ecommerce.caramellabeachclub.entities.MetodoDiPagamento;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidOperationException;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidQuantityException;
import org.ecommerce.caramellabeachclub.resources.exceptions.ProductNotFoundException;
import org.ecommerce.caramellabeachclub.resources.exceptions.UserNotFoundException;
import org.ecommerce.caramellabeachclub.services.CarrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrello")
public class CarrelloController {

    @Autowired
    private CarrelloService carrelloService;



    // Aggiungi un prodotto al carrello
    @PostMapping("/aggiungi")
    public ResponseEntity<String> aggiungiAlCarrello(
            @RequestParam int idUtente,
            @RequestParam int idProdotto,
            @RequestParam int quantita) {
        try {
            carrelloService.aggiungiAlCarrello(idUtente, idProdotto, quantita);
            return ResponseEntity.ok("Prodotto aggiunto al carrello con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("Utente non trovato.");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(404).body("Prodotto non trovato.");
        } catch (InvalidQuantityException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Stampa l'errore completo nei log
            return ResponseEntity.status(500).body("Errore interno del server.");
        }
    }


    // Aumenta la quantità di un prodotto nel carrello
    @PutMapping("/plus")
    public ResponseEntity<String> plusAdding(
            @RequestParam int idUtente,
            @RequestParam int idProdotto) {
        try {
            carrelloService.plusAdding(idUtente, idProdotto);
            return ResponseEntity.ok("Quantità aumentata con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("Utente non trovato.");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(404).body("Prodotto non trovato.");
        } catch (InvalidQuantityException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Rimuovi un prodotto dal carrello
    @DeleteMapping("/rimuovi")
    public ResponseEntity<String> rimuoviDalCarrello(
            @RequestParam int idUtente,
            @RequestParam int idProdotto) {
        try {
            carrelloService.rimuoviDalCarrello(idUtente, idProdotto);
            return ResponseEntity.ok("Prodotto rimosso dal carrello con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("Utente non trovato.");
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Diminuisci la quantità di un prodotto nel carrello
    @PutMapping("/minus")
    public ResponseEntity<String> minusRemoving(
            @RequestParam int idUtente,
            @RequestParam int idProdotto) {
        try {
            carrelloService.minusRemoving(idUtente, idProdotto);
            return ResponseEntity.ok("Quantità ridotta con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("Utente non trovato.");
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Svuota il carrello
    @DeleteMapping("/svuota")
    public ResponseEntity<String> svuotaCarrello(@RequestParam int idUtente) {
        try {
            carrelloService.svuotaCarrello(idUtente);
            return ResponseEntity.ok("Carrello svuotato con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("Utente non trovato.");
        }
    }

    // Effettua un ordine
    @PostMapping("/ordina")
    public ResponseEntity<String> ordina(
            @RequestParam int idUtente,
            @RequestParam int metodoPagamento,
            @RequestParam String indirizzoSpedizione) {
        try {
            carrelloService.ordina(idUtente, metodoPagamento, indirizzoSpedizione);
            return ResponseEntity.ok("Ordine effettuato con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("Utente non trovato.");
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
