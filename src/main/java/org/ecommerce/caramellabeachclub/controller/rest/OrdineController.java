package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.entities.Ordine;
import org.ecommerce.caramellabeachclub.entities.Reso;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.services.OrdineService;
import org.ecommerce.caramellabeachclub.repositories.OrdineRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/{id}/annulla")
    public ResponseEntity<String> annullaOrdine(
            @PathVariable Integer id,
            @RequestParam Integer idUtente,
            @RequestParam String motivo) {

        Utente utente = utenteRepository.findById(idUtente).orElse(null);
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
    @PostMapping("/{id}/reso")
    public ResponseEntity<String> effettuaReso(
            @PathVariable Integer id,
            @RequestParam Integer idUtente,
            @RequestParam String motivo) {

        Utente utente = utenteRepository.findById(idUtente).orElse(null);
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
    @PostMapping("/{id}/reso/annulla")
    public ResponseEntity<String> annullaReso(
            @PathVariable Integer id,
            @RequestParam Integer idUtente,
            @RequestParam Integer idReso) {

        Utente utente = utenteRepository.findById(idUtente).orElse(null);
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
}