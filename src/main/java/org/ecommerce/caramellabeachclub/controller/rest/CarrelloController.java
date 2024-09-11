package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.services.CarrelloService;
import org.ecommerce.caramellabeachclub.resources.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/carrello")
public class CarrelloController {

    @Autowired
    private CarrelloService carrelloService;

    // Aggiungi un prodotto al carrello
    @PostMapping("/aggiungi")
    public ResponseEntity<String> aggiungiAlCarrello(@RequestParam int idUtente,
                                                     @RequestBody Prodotto prodotto,
                                                     @RequestParam int quantita) {
        try {
            carrelloService.aggiungiAlCarrello(idUtente, prodotto, quantita);
            return ResponseEntity.ok("Prodotto aggiunto al carrello con successo!");
        } catch (UserNotFoundException | ProductNotFoundException | InvalidQuantityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Incrementa la quantità di un prodotto nel carrello (Plus Adding)
    @PutMapping("/incrementa")
    public ResponseEntity<String> incrementaQuantita(@RequestParam int idUtente,
                                                     @RequestBody CarrelloProdotto carrelloProdotto) {
        try {
            carrelloService.plusAdding(idUtente, carrelloProdotto);
            return ResponseEntity.ok("Quantità del prodotto incrementata nel carrello.");
        } catch (UserNotFoundException | ProductNotFoundException | InvalidQuantityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Rimuovi un prodotto dal carrello
    @DeleteMapping("/rimuovi")
    public ResponseEntity<String> rimuoviDalCarrello(@RequestParam int idUtente,
                                                     @RequestBody CarrelloProdotto carrelloProdotto) {
        try {
            carrelloService.rimuoviDalCarrello(idUtente, carrelloProdotto);
            return ResponseEntity.ok("Prodotto rimosso dal carrello.");
        } catch (UserNotFoundException | InvalidOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Decrementa la quantità di un prodotto nel carrello (Minus Removing)
    @PutMapping("/decrementa")
    public ResponseEntity<String> decrementaQuantita(@RequestParam int idUtente,
                                                     @RequestBody CarrelloProdotto carrelloProdotto) {
        try {
            carrelloService.minusRemoving(idUtente, carrelloProdotto);
            return ResponseEntity.ok("Quantità del prodotto decrementata nel carrello.");
        } catch (UserNotFoundException | InvalidOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Svuota completamente il carrello
    @DeleteMapping("/svuota")
    public ResponseEntity<String> svuotaCarrello(@RequestParam int idUtente) {
        try {
            carrelloService.svuotaCarrello(idUtente);
            return ResponseEntity.ok("Carrello svuotato con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Effettua un ordine
    @PostMapping("/ordina")
    public ResponseEntity<String> effettuaOrdine(@RequestParam int idUtente,
                                                 @RequestBody MetodoDiPagamento metodoDiPagamento,
                                                 @RequestParam String indirizzoSpedizione) {
        try {
            carrelloService.ordina(idUtente, metodoDiPagamento, indirizzoSpedizione);
            return ResponseEntity.ok("Ordine effettuato con successo!");
        } catch (UserNotFoundException | InvalidOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}