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

    // GET - Recuperare tutti gli ordini
    @GetMapping
    public ResponseEntity<List<Ordine>> getAllOrdini() {
        List<Ordine> ordini = ordineRepository.findAll();
        return new ResponseEntity<>(ordini, HttpStatus.OK);
    }

    // GET - Recuperare un ordine specifico per ID
    @GetMapping("/{id}")
    public ResponseEntity<Ordine> getOrdineById(@PathVariable Integer id) {
        return ordineRepository.findById(id)
                .map(ordine -> new ResponseEntity<>(ordine, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST - Creare un nuovo ordine
    @PostMapping
    public ResponseEntity<Ordine> createOrdine(@RequestBody Ordine ordine) {
        Ordine nuovoOrdine = ordineRepository.save(ordine);
        return new ResponseEntity<>(nuovoOrdine, HttpStatus.CREATED);
    }

    // PUT - Aggiornare un ordine esistente
    @PutMapping("/{id}")
    public ResponseEntity<Ordine> updateOrdine(@PathVariable Integer id, @RequestBody Ordine ordine) {
        return ordineRepository.findById(id).map(ordineEsistente -> {
            ordineEsistente.setStato(ordine.getStato());
            ordineEsistente.setData(ordine.getData());
            ordineEsistente.setOra(ordine.getOra());
            ordineRepository.save(ordineEsistente);
            return new ResponseEntity<>(ordineEsistente, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE - Cancellare un ordine
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdine(@PathVariable Integer id) {
        return ordineRepository.findById(id).map(ordine -> {
            ordineRepository.delete(ordine);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST - Annullare un ordine
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

    // POST - Effettuare un reso per un ordine
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

    // POST - Annullare un reso per un ordine
    @PostMapping("/{id}/reso/annulla")
    public ResponseEntity<String> annullaReso(
            @PathVariable Integer id,
            @RequestParam Integer idUtente,
            @RequestParam Integer idReso) {

        Utente utente = utenteRepository.findById(idUtente).orElse(null);
        Ordine ordine = ordineRepository.findById(id).orElse(null);
        Reso reso = ordine.getResos(); // Otteniamo il reso associato all'ordine

        if (utente == null || ordine == null || reso == null) {
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
