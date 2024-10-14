package org.ecommerce.caramellabeachclub.controller.rest;

import jakarta.validation.constraints.*;
import org.ecommerce.caramellabeachclub.dto.CarrelloProdottoDTO;
import org.ecommerce.caramellabeachclub.jwt.CustomJwt;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidOperationException;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidQuantityException;
import org.ecommerce.caramellabeachclub.resources.exceptions.ProductNotFoundException;
import org.ecommerce.caramellabeachclub.resources.exceptions.UserNotFoundException;
import org.ecommerce.caramellabeachclub.services.CarrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/carrello")
public class CarrelloController {

    @Autowired
    private CarrelloService carrelloService;

    @Autowired
    private UtenteRepository utenteRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/aggiungi")
    public ResponseEntity<Map<String, String>> aggiungiAlCarrello(
            @RequestParam @NotNull @Positive int idProdotto,
            @RequestParam @NotNull @Positive int quantita) {

        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            carrelloService.aggiungiAlCarrello(email, idProdotto, quantita);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Prodotto aggiunto al carrello con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (ProductNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Prodotto non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidQuantityException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Errore interno del server.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/items")
    public ResponseEntity<List<CarrelloProdottoDTO>> getCartItems() {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            List<CarrelloProdottoDTO> cartItems = carrelloService.getCartItemsByEmail(email);
            return ResponseEntity.ok(cartItems);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    // Aumenta la quantità di un prodotto nel carrello
    @PutMapping("/plus")
    public ResponseEntity<Map<String, String>> plusAdding(
            @RequestParam @NotNull @Positive int idProdotto) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();
        try {
            carrelloService.plusAdding(email, idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quantità aumentata con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (ProductNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Prodotto non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidQuantityException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    // Rimuovi un prodotto dal carrello
    @DeleteMapping("/rimuovi")
    public ResponseEntity<Map<String, String>> rimuoviDalCarrello(
            @RequestParam @NotNull @Positive int idProdotto) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            carrelloService.rimuoviDalCarrello(email, idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Prodotto rimosso dal carrello con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    // Diminuisce la quantità di un prodotto nel carrello
    @PutMapping("/minus")
    public ResponseEntity<Map<String, String>> minusRemoving(
            @RequestParam @NotNull @Positive int idProdotto) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            carrelloService.minusRemoving(email, idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quantità ridotta con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    // Svuota il carrello
    @DeleteMapping("/svuota")
    public ResponseEntity<Map<String, String>> svuotaCarrello() {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            carrelloService.svuotaCarrello(email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Carrello svuotato con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        }
    }

    // Effettua un ordine
    @PostMapping("/ordina")
    public ResponseEntity<Map<String, String>> ordina(
            @RequestParam @NotNull @Min(1) int metodoPagamento,
            @RequestParam @NotNull String indirizzoSpedizione) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();

        try {
            carrelloService.ordina(email, metodoPagamento, indirizzoSpedizione);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ordine effettuato con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }
}
