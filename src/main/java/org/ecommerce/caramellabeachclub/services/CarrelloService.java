package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.CarrelloProdottoRepository;
import org.ecommerce.caramellabeachclub.repositories.CarrelloRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.ecommerce.caramellabeachclub.resources.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Random;

@Service
public class CarrelloService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Autowired
    private CarrelloProdottoRepository carrelloProdottoRepository;

    private static final Random RANDOM = new Random();

    @Transactional
    public void aggiungiAlCarrello(int idUtente, Prodotto p, int quantita)
            throws UserNotFoundException, InvalidQuantityException {

        Utente user = utenteRepository.getUtenteById(idUtente);

        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setIdUtente(user);
            carrello = carrelloRepository.save(carrello);
        }

        if (p.getDisp() < quantita) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: il prodotto non è disponibile per la quantità desiderata");
        }

        CarrelloProdotto aggiunta = new CarrelloProdotto();
        aggiunta.setCarrello(carrello);
        aggiunta.setProdotto(p);
        aggiunta.setQuantita(quantita);
        carrelloProdottoRepository.save(aggiunta);
    }// Metodo per aggiungere al carrello

    @Transactional
    public void plusAdding(int idUtente, CarrelloProdotto cp)
            throws UserNotFoundException, InvalidQuantityException {

        utenteRepository.getUtenteById(idUtente);

        Prodotto p = cp.getProdotto();
        if (p.getDisp() < 1) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: il prodotto non è disponibile per la quantità desiderata");
        }

        cp.setQuantita(cp.getQuantita() + 1);
        carrelloProdottoRepository.save(cp);
    }// Metodo per aggiungere al carrello da un pulsantino '+'

    @Transactional
    public void rimuoviDalCarrello(int idUtente, CarrelloProdotto cp)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.getUtenteById(idUtente);
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        if (!cp.getCarrello().equals(carrello)) {
            throw new InvalidOperationException("Impossibile proseguire con l'operazione");
        }

        carrelloProdottoRepository.delete(cp);
    }// Metodo per rimuovere dal carrello

    @Transactional
    public void minusRemoving(int idUtente, CarrelloProdotto cp)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.getUtenteById(idUtente);
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        if (!cp.getCarrello().equals(carrello)) {
            throw new InvalidOperationException("Impossibile proseguire con l'operazione");
        }

        if (cp.getQuantita() > 1) {
            cp.setQuantita(cp.getQuantita() - 1);
            carrelloProdottoRepository.save(cp);
        } else {
            carrelloProdottoRepository.delete(cp);
        }
    }// Metodo per rimuovere dal carrello da un pulsantino '-'

    @Transactional
    public void svuotaCarrello(int idUtente)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.getUtenteById(idUtente);
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        carrelloProdottoRepository.deleteAll(carrello.getCarrelloProdottos());
        carrelloRepository.delete(carrello);
    }// Metodo per svuotare il carrello

    @Transactional
    public void ordina(int idUtente, MetodoDiPagamento metodoDiPagamento)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.getUtenteById(idUtente);
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        Ordine ordine = new Ordine();
        Transazione transazione = new Transazione();

        ordine.setIdCarrello(carrello);
        ordine.setIdUtente(user);
        ordine.setOra(LocalTime.now());
        ordine.setStato("Processamento in corso...");

        transazione.setMetodoDiPagamento(metodoDiPagamento);
        transazione.setIdOrdine(ordine);
        transazione.setOra(LocalTime.now());
        transazione.setData(Instant.now());
        transazione.setImporto(calcolaImporto(carrello));

        boolean esitoPagamento = processaPagamento(metodoDiPagamento, transazione.getImporto());

        if (esitoPagamento) {
            transazione.setEsito(true);
            ordine.setStato("Pagamento completato");
        } else {
            transazione.setEsito(false);
            ordine.setStato("Pagamento fallito");
        }
    }// Metodo per eseguire un ordine di un carrello

    private static BigDecimal calcolaImporto(Carrello carrello) {
        return carrello.getCarrelloProdottos().stream()
                .map(cp -> cp.getProdotto().getPrezzo().multiply(BigDecimal.valueOf(cp.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }// Metodo per calcolare l'importo complessivo dei prodotti in un carrello


    private boolean processaPagamento(MetodoDiPagamento metodoDiPagamento, BigDecimal amount) {
        System.out.println("Pagamento in corso con il metodo selezionato: " + metodoDiPagamento.getSelezione());
        System.out.println("Importo addebitato: " + amount);

        boolean paymentSuccess = RANDOM.nextInt(100) < 80;

        if (paymentSuccess) {
            System.out.println("L'operazione di pagamento ha avuto successo!");
            return true;
        } else {
            System.out.println("Pagamento non andato a buon fine.");
            return false;
        }
    }// Metodo che simula il processamento di un pagamento



}
