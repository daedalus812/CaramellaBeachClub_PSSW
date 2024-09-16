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

    // finocchio ricorda: aggiungi un prodotto al carrello
    @PostMapping("/aggiungi")
    public ResponseEntity<String> aggiungiAlCarrello(@RequestParam int idUtente,
                                                     @RequestParam int idProdotto,
                                                     @RequestParam int quantita,
                                                    @RequestBody Carrello carrello
    ) {

        try {
            carrelloService.aggiungiAlCarrello(idUtente, idProdotto, quantita);
            return ResponseEntity.ok("Prodotto aggiunto al carrello con successo!");
        } catch (UserNotFoundException | ProductNotFoundException | InvalidQuantityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // *fatto* (Plus Adding)
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

    @DeleteMapping("/svuota")
    public ResponseEntity<String> svuotaCarrello(@RequestParam int idUtente) {
        try {
            carrelloService.svuotaCarrello(idUtente);
            return ResponseEntity.ok("Carrello svuotato con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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
