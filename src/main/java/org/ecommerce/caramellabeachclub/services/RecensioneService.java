package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private ProdottiOrdinatiRepository prodottiOrdinatiRepository;

    public void aggiungiRecensione(int utente, int ordine, int prodotto, String commento, String valutazione) {
        Utente user = utenteRepository.findById(utente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Ordine ord = ordineRepository.findById(ordine)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato"));

        if (ord.getIdUtente() != user.getId()) {
            throw new IllegalArgumentException("Utente non associato all'ordine");
        }

        if (!(ord.getStato().equals("Completato"))) {
            throw new IllegalArgumentException("Non puoi ancora recensire questo oggetto");
        }

        ProdottiOrdinati pO = prodottiOrdinatiRepository.findProdottiOrdinatiByIdProdotto(prodotto);
        if (pO == null) {
            throw new IllegalArgumentException("Non puoi ancora recensire questo oggetto");
        }

        Recensione recensione = new Recensione();
        recensione.setIdProdotto(prodotto);
        recensione.setIdUtente(user.getId());
        recensione.setData(Instant.now());
        recensione.setValutazione(valutazione);
        recensione.setCommento(commento);
        recensioneRepository.save(recensione);
        System.out.println("Recensione aggiunta con successo");

    }

    public void eliminaRecensione(int idRecensione, int utenteId) {

        Recensione recensione = recensioneRepository.findById(idRecensione)
                .orElseThrow(() -> new IllegalArgumentException("Recensione non trovata"));

        // Controllo se l'utente è autorizzato (deve essere lo stesso utente che ha scritto la recensione)
        if (recensione.getIdUtente() != utenteId) {
            throw new IllegalArgumentException("Non sei autorizzato a eliminare questa recensione");
        }

        recensioneRepository.delete(recensione);
        System.out.println("Recensione eliminata con successo");
    }
}