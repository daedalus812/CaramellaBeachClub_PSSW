package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.dto.OrdineDTO;
import org.ecommerce.caramellabeachclub.entities.Ordine;
import org.ecommerce.caramellabeachclub.entities.Reso;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.jwt.CustomJwt;
import org.ecommerce.caramellabeachclub.resources.exceptions.UserNotFoundException;
import org.ecommerce.caramellabeachclub.services.OrdineService;
import org.ecommerce.caramellabeachclub.repositories.OrdineRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/ordini")
public class OrdineController {

    @Autowired
    private OrdineService ordineService;

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private UtenteRepository utenteRepository;


    /*

    Il metodo che effettua un ordine si trova all'interno della logica del Carrello, quindi
    CarrelloService e CarrelloController.

     */

    // Annulla un ordine

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/annulla")
    public ResponseEntity<String> annullaOrdine(
            @PathVariable Integer id,
            @RequestParam String motivo) {

        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();
        Utente utente = utenteRepository.findByEmail(email);
        Ordine ordine = ordineRepository.findById(id).orElse(null);

        if (utente == null || ordine == null) {
            return new ResponseEntity<>("Utente o Ordine non trovati.", HttpStatus.NOT_FOUND);
        }


        try {
            ordineService.annullaOrdine(utente, ordine, motivo);
            return new ResponseEntity<>("Ordine annullato con successo.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'annullamento dell'ordine: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // Effettua un reso per un ordine

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/reso")
    public ResponseEntity<String> effettuaReso(
            @PathVariable Integer id,
            @RequestParam String motivo) {

        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();
        Utente utente = utenteRepository.findByEmail(email);
        Ordine ordine = ordineRepository.findById(id).orElse(null);

        if (utente == null || ordine == null) {
            return new ResponseEntity<>("Utente o Ordine non trovati.", HttpStatus.NOT_FOUND);
        }

        try {
            ordineService.effettuaReso(utente, ordine, motivo);
            return new ResponseEntity<>("Reso effettuato con successo.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante il reso: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Annulla un reso per un ordine

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/reso/annulla")
    public ResponseEntity<String> annullaReso(
            @PathVariable Integer id,
            @RequestParam Integer idReso) {

        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();
        Utente utente = utenteRepository.findByEmail(email);
        Ordine ordine = ordineRepository.findById(id).orElse(null);
        assert ordine != null;
        Reso reso = ordine.getResos();

        if (utente == null || reso == null) {
            return new ResponseEntity<>("Utente, Ordine o Reso non trovati.", HttpStatus.NOT_FOUND);
        }

        try {
            ordineService.annullaReso(utente, ordine, reso);
            return new ResponseEntity<>("Reso annullato con successo.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'annullamento del reso: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/miei-ordini")
    public ResponseEntity<List<OrdineDTO>> getMieiOrdini() {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        String email = jwt.getName();


        try {
            List<OrdineDTO> ordini = ordineService.getOrdiniByEmail(email);
            return ResponseEntity.ok(ordini);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}