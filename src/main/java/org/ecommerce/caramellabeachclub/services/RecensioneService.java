package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.OrdineRepository;
import org.ecommerce.caramellabeachclub.repositories.RecensioneRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RecensioneService {
    @Autowired
    RecensioneRepository recensioneRepository;
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private OrdineRepository ordineRepository;

    public void aggiungiRecensione(Utente utente, Prodotto prodottoDaRecensire, String valutazione, String commento){

        // Verificare che l'utente abbia effettuato un ordine con all'interno un prodotto da recensire
        // altrimenti la recensione è da ritenersi fasulla

        // La logica è che un ordine si effettua su un carrello che contiene dei prodotti.
        // Se il carrello ordinato contiene il prodotto da recensire allora siamo apposto.

        Recensione recensione = new Recensione();

        Ordine ordine = ordineRepository.findByUtenteId(utente.getId());

        // Mi prendo il carrello ordinato
        Carrello carrello = ordine.getIdCarrello();

        // Metto in un set tutti i "CarrelloProdotti" ordinati
        Set<CarrelloProdotto> carrelloProdottos = carrello.getCarrelloProdottos();

        // Creo una lista in cui ci saranno tutti i prodotti di quel carrello ordinato
        ArrayList<Prodotto> prodottiOrdinati = new ArrayList<>();

        for (CarrelloProdotto cp : carrelloProdottos){
            prodottiOrdinati.add(cp.getProdotto());
        }

        for (Prodotto p : prodottiOrdinati){
            if (!(p.equals(prodottoDaRecensire))){
                throw new InvalidOperationException("Non puoi recensire un prodotto che non hai ordinato!");
            }
        }

        recensione.setIdUtente(utente);
        recensione.setCommento(commento);
        recensione.setValutazione(valutazione);
        recensione.setData(Instant.now());
        recensione.setIdProdotto(prodottoDaRecensire);
        recensioneRepository.save(recensione);

    }

}
