package org.ecommerce.caramellabeachclub.controller.rest;

import jakarta.validation.constraints.*;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidOperationException;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidQuantityException;
import org.ecommerce.caramellabeachclub.resources.exceptions.ProductNotFoundException;
import org.ecommerce.caramellabeachclub.resources.exceptions.UserNotFoundException;
import org.ecommerce.caramellabeachclub.services.CarrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrello")
public class CarrelloController {

    @Autowired
    private CarrelloService carrelloService;

    // Aggiungi al carrello
    @PostMapping("/aggiungi")
    public ResponseEntity<String> aggiungiAlCarrello(
            @RequestParam @NotNull @Positive int idProdotto,
            @RequestParam @NotNull @Positive int quantita) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Utente currentUser = (Utente) authentication.getPrincipal();
        int idUtente = currentUser.getId();

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
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore interno del server.");
        }
    }


    // Plus 1 adding
    @PutMapping("/plus")
    public ResponseEntity<String> plusAdding(
            @RequestParam @NotNull @Positive int idProdotto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Utente currentUser = (Utente) authentication.getPrincipal();
        int idUtente = currentUser.getId();
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
            @RequestParam @NotNull @Positive int idProdotto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Utente currentUser = (Utente) authentication.getPrincipal();
        int idUtente = currentUser.getId();
        try {
            carrelloService.rimuoviDalCarrello(idUtente, idProdotto);
            return ResponseEntity.ok("Prodotto rimosso dal carrello con successo.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("Utente non trovato.");
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Minus 1 removing
    @PutMapping("/minus")
    public ResponseEntity<String> minusRemoving(
            @RequestParam @NotNull @Positive int idProdotto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Utente currentUser = (Utente) authentication.getPrincipal();
        int idUtente = currentUser.getId();
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
    public ResponseEntity<String> svuotaCarrello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Utente currentUser = (Utente) authentication.getPrincipal();
        int idUtente = currentUser.getId();
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
            @RequestParam @NotNull @Min(1) int metodoPagamento,
            @RequestParam @NotNull String indirizzoSpedizione) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Utente currentUser = (Utente) authentication.getPrincipal();
        int idUtente = currentUser.getId();
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