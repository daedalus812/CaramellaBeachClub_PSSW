//CHECK DONE, OK!
package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.OrdineRepository;
import org.ecommerce.caramellabeachclub.repositories.RecensioneRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidOperationException;
import org.ecommerce.caramellabeachclub.resources.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Set;

@Service
public class RecensioneService {
    @Autowired
    RecensioneRepository recensioneRepository;
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private OrdineRepository ordineRepository;

    public void aggiungiRecensione(Integer ute, Integer ord, Prodotto prodottoDaRecensire, String valutazione, String commento){

        // Verificare che l'utente abbia effettuato un ordine con all'interno un prodotto da recensire
        // altrimenti la recensione è da ritenersi fasulla

        // La logica è che un ordine si effettua su un carrello che contiene dei prodotti.
        // Se il carrello ordinato contiene il prodotto da recensire allora siamo apposto.

        // Eseguo una validazione dei parametri in ingresso
        if (ute == null || ord == null || prodottoDaRecensire == null || valutazione == null || commento == null) {
            throw new IllegalArgumentException("Dati inconsistenti!");
        }


        // Eseguo un recupero e una validazione dell'utente dal database
        Utente utente = utenteRepository.findById(ute).orElseThrow(UserNotFoundException::new);
        if (!utente.getId().equals(ute)) {
            throw new InvalidOperationException("Dati inconsistenti! Aggiorna la pagina.");
        }

        // Recupero e validazione dell'ordine dal database
        Ordine ordine = ordineRepository.findById(ord).orElseThrow(() ->
                new InvalidOperationException("Ordine non trovato"));
        if (!ordine.getId().equals(ord)) {
            throw new InvalidOperationException("Dati inconsistenti! Aggiorna la pagina.");
        }

        // Verifica che l'ordine appartenga all'utente specificato
        if (!ordine.getUtente().equals(utente)) {
            throw new InvalidOperationException("Operazione non valida: l'utente non è associato a questo ordine");
        }

        // Verifica che il prodotto da recensire sia presente nel carrello ordinato
        Carrello carrello = ordine.getIdCarrello();
        Set<CarrelloProdotto> prodottiNelCarrello = carrello.getCarrelloProdottos();

        boolean prodottoTrovato = false;
        for (CarrelloProdotto cp : prodottiNelCarrello){
            if (cp.getProdotto().equals(prodottoDaRecensire)) {
                prodottoTrovato = true;
                break;
            }
        }

        if (!prodottoTrovato) {
            throw new InvalidOperationException("Non puoi recensire un prodotto che non hai ordinato!");
        }

        // Creazione della recensione e salvataggio nel database
        Recensione recensione = new Recensione();
        recensione.setUtente(utente);
        recensione.setCommento(commento);
        recensione.setValutazione(valutazione);
        recensione.setData(Instant.now());
        recensione.setIdProdotto(prodottoDaRecensire);
        recensioneRepository.save(recensione);
    }

}