package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.resources.other.*;
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
import java.util.Set;


@Service
public class CarrelloService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Autowired
    private CarrelloProdottoRepository carrelloProdottoRepository;

    @Transactional
    public void aggiungiAlCarrello(int idUtente, Prodotto p, int quantita)
            throws UserNotFoundException, InvalidQuantityException {

        // Verifico che l'utente esista
        Utente user = utenteRepository.getUtenteById(idUtente);
        if (user == null) {
            throw new UserNotFoundException("Per proseguire devi prima loggarti!");
        }

        // Verifico che esista un carrello per quell'utente, altrimenti se è vuoto lo creo
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setIdUtente(user);
            carrello = carrelloRepository.save(carrello);
        }

        // Verifico la disponibilità del prodotto
        if (p.getDisp() < quantita) {
            throw new InvalidQuantityException
                    ("Impossibile aggiungere al carrello: il prodotto non è disponibile per la quantità desiderata");
        }

        // Creo l'aggiunta e la collego al carrello dell'utente
        CarrelloProdotto aggiunta = new CarrelloProdotto();
        aggiunta.setCarrello(carrello);
        aggiunta.setProdotto(p);
        aggiunta.setQuantita(quantita);
        carrelloProdottoRepository.save(aggiunta);

    }//---aggiungi al carrello---


    public void plusAdding(int idUtente, CarrelloProdotto cp)
            throws UserNotFoundException, InvalidQuantityException{

        // Verifico che l'utente esista
        Utente user = utenteRepository.getUtenteById(idUtente);
        if (user == null) {
            throw new UserNotFoundException("Per proseguire devi prima loggarti!");
        }

        // Verifico la disponibilità del prodotto
        Prodotto p = cp.getProdotto();
        if (p.getDisp() < 1) {
            throw new InvalidQuantityException
                    ("Impossibile aggiungere al carrello: il prodotto non è disponibile per la quantità desiderata");
        }

        cp.setQuantita(cp.getQuantita()+1);
        carrelloProdottoRepository.save(cp);

    }//---Aggiunta al carrello tramite pulsantino "+"---


    public void rimuoviDalCarrello(int idUtente, CarrelloProdotto cp)
            throws UserNotFoundException, InvalidOperationException {

        // Verifico che l'utente esista
        Utente user = utenteRepository.getUtenteById(idUtente);
        if (user == null) { throw new UserNotFoundException("Per proseguire devi prima loggarti!"); }

        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        // Ulteriori verifiche
        if (carrello == null) {
            throw new InvalidOperationException("Impossibile proseguire con l'operazione"); }
        if (!(cp.getCarrello().equals(carrello))) {
            throw new InvalidOperationException("Impossibile proseguire con l'operazione"); }

        carrelloProdottoRepository.delete(cp);
        carrelloRepository.save(carrello);
        

    }// Rimuovi dal carrello

    public void minusRemoving(int idUtente, CarrelloProdotto cp)
            throws UserNotFoundException, InvalidOperationException {

        // Verifico che l'utente esista
        Utente user = utenteRepository.getUtenteById(idUtente);
        if (user == null) { throw new UserNotFoundException("Per proseguire devi prima loggarti!"); }

        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        // Ulteriori verifiche
        if (carrello == null) {
            throw new InvalidOperationException("Impossibile proseguire con l'operazione"); }
        if (!(cp.getCarrello().equals(carrello))) {
            throw new InvalidOperationException("Impossibile proseguire con l'operazione"); }

        cp.setQuantita(cp.getQuantita()-1);

        carrelloProdottoRepository.delete(cp);
        carrelloRepository.save(carrello);


    }//---Rimozione dal carrello tramite pulsantino "-"---

    public void svuotaCarrello (int idUtente)
            throws UserNotFoundException, InvalidOperationException {

        // Verifico che l'utente esista
        Utente user = utenteRepository.getUtenteById(idUtente);
        if (user == null) {
            throw new UserNotFoundException("Per proseguire devi prima loggarti!");
        }

        // Verifico che il carrello non sia già vuoto
        Carrello carrello = carrelloRepository.findByIdUtente(idUtente);

        // La gestione di questa eccezione può anche avvenire rendendo non cliccabile il tasto "Svuota Carrello"
        if (carrello==null) { throw new InvalidOperationException("Il carrello è già vuoto!"); }

        carrelloRepository.delete(carrello);
        carrelloRepository.save(carrello);

    }//---Svuota Carrello---

    public void ordina(Utente utente, MetodoDiPagamento metodoDiPagamento){

        // Verifico che l'utente esista
        Utente user = utenteRepository.getUtenteById(utente.getId());
        if (user == null) {
            throw new UserNotFoundException("Per proseguire devi prima loggarti!");
        }

        // Verifico che il carrello non sia vuoto
        Carrello carrello = carrelloRepository.findByIdUtente(utente.getId());

        // La gestione di questa eccezione può anche avvenire rendendo non cliccabile il tasto "Procedi all'ordine"
        if (carrello==null) { throw new InvalidOperationException("Il carrello è vuoto!"); }

        Ordine ordine = new Ordine();
        Transazione transazione = new Transazione();

        ordine.setIdCarrello(carrelloRepository.findByIdUtente(utente.getId()));
        ordine.setIdUtente(utente);
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

    }//---Ordina---

    private BigDecimal calcolaImporto(Carrello carrello) {
        BigDecimal importoTotale = BigDecimal.ZERO;
        Set<CarrelloProdotto> prodottiNelCarrello = carrello.getCarrelloProdottos();

        for (CarrelloProdotto carrelloProdotto : prodottiNelCarrello) {
            BigDecimal prezzoProdotto = carrelloProdotto.getProdotto().getPrezzo();
            int quantita = carrelloProdotto.getQuantita();
            BigDecimal importoProdotto = prezzoProdotto.multiply(BigDecimal.valueOf(quantita));
            importoTotale = importoTotale.add(importoProdotto);
        }

        return importoTotale;
    } //---Calcolo Importo---

    private static final Random RANDOM = new Random();

    public boolean processaPagamento(MetodoDiPagamento metodoDiPagamento, BigDecimal amount) {
        // Simulazione del processo di pagamento
        // Integrazione con un servizio di pagamento

        // Logica di esempio per simulare un pagamento
        System.out.println("Pagamento in corso con il metodo selezionato: " + metodoDiPagamento.getSelezione());
        System.out.println("Importo addebitato: " + amount);

        // Simuliamo una probabilità di successo dell'80%
        boolean paymentSuccess = RANDOM.nextInt(100) < 80;

        if (paymentSuccess) {
            System.out.println("Payment processed successfully.");
            return true;
        } else {
            System.out.println("Payment failed.");
            return false;
        }
    }
}
