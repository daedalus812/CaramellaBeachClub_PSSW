package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.Recensione;
import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.repositories.RecensioneRepository;
import org.ecommerce.caramellabeachclub.repositories.ProdottoRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RecensioneService {

    private final RecensioneRepository recensioneRepository;
    private final ProdottoRepository prodottoRepository;
    private final UtenteRepository utenteRepository;

    public RecensioneService(RecensioneRepository recensioneRepository, ProdottoRepository prodottoRepository, UtenteRepository utenteRepository) {
        this.recensioneRepository = recensioneRepository;
        this.prodottoRepository = prodottoRepository;
        this.utenteRepository = utenteRepository;
    }

    // Salva una recensione
    public Recensione saveRecensione(Integer prodottoId, Integer utenteId, String valutazione, String commento) {
        Optional<Prodotto> prodotto = prodottoRepository.findById(prodottoId);
        Optional<Utente> utente = utenteRepository.findById(utenteId);

        if (prodotto.isPresent() && utente.isPresent()) {
            Recensione recensione = new Recensione();
            recensione.setIdProdotto(prodotto.get());
            recensione.setUtente(utente.get());
            recensione.setValutazione(valutazione);
            recensione.setCommento(commento);
            recensione.setData(Instant.now());
            return recensioneRepository.save(recensione);
        } else {
            throw new IllegalArgumentException("Prodotto o utente non trovati");
        }
    }

    // Recupera tutte le recensioni per un prodotto
    public List<Recensione> getRecensioniByProdotto(Integer prodottoId) {
        Optional<Prodotto> prodotto = prodottoRepository.findById(prodottoId);
        if (prodotto.isPresent()) {
            return recensioneRepository.findByIdProdotto(prodotto.get());
        } else {
            throw new IllegalArgumentException("Prodotto non trovato");
        }
    }

    // Recupera tutte le recensioni di un utente
    public List<Recensione> getRecensioniByUtente(Integer utenteId) {
        Optional<Utente> utente = utenteRepository.findById(utenteId);
        if (utente.isPresent()) {
            return recensioneRepository.findByUtente(utente.get());
        } else {
            throw new IllegalArgumentException("Utente non trovato");
        }
    }

}
