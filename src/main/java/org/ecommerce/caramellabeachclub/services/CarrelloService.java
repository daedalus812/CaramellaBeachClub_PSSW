//CHECK DONE, OK!
package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.*;
import org.ecommerce.caramellabeachclub.resources.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
public class CarrelloService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Autowired
    private CarrelloProdottoRepository carrelloProdottoRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;

    private static final Random RANDOM = new Random();

    @Transactional
    public void aggiungiAlCarrello(int idUtente, Prodotto p, int quantita)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException {

        // Recupera l'utente dal database
        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupera il carrello dell'utente o creane uno nuovo se non esiste
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setIdUtente(user);
            carrello = carrelloRepository.save(carrello);
        }

        // La variabile prod serve affinché l'oggetto che il client mi passa venga prelevato
        // dal database, e che quindi esista.
        Prodotto prod = prodottoRepository.findById(p.getId()).orElseThrow(ProductNotFoundException::new);

        // Mi prendo la disponibilità dell'oggetto dal database, grazie alla metodo(query) di ProdottoRepository
        int disponibilitaProd = prodottoRepository.findDisponibilitaById(prod.getId());

        // Verifico che la quantità desiderata sia disponibile (secondo la quantità che il database mi dà)
        if (quantita > disponibilitaProd) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: il prodotto non è disponibile per la quantità desiderata");
        }

        // Ipotizziamo che l'aggiunta si trovi già all'interno del carrello
        // Ciò che faremo è sommare la quantità selezionata a quella già presente nel carrello
        // La disponibilità di un oggetto x (visibile ad altri utenti) non verrà ridotta solo perché si è aggiunto
        // al carrello: va ordinato affinché la disponibilità si riduca. Pertanto il controllo sulla disponibilità
        // di un oggetto che è già presente nel carrello, va fatta considerando la quantità selezionata + quella già presente
        // nel carrello
        CarrelloProdotto aggiunta = carrelloProdottoRepository.findByCarrelloAndProdotto(carrello, prod);


        if (aggiunta.getId() != null) {
            int nuovaQuantita = aggiunta.getQuantita() + quantita;
            if (nuovaQuantita > disponibilitaProd) {
                throw new InvalidQuantityException("Impossibile aggiungere al carrello: il prodotto non è disponibile per la quantità desiderata");
            }
            aggiunta.setQuantita(nuovaQuantita);
        } else {
            aggiunta.setCarrello(carrello);
            aggiunta.setProdotto(prod);
            aggiunta.setQuantita(quantita);
        }

        carrelloProdottoRepository.save(aggiunta);
    }

    @Transactional
    public void plusAdding(int idUtente, CarrelloProdotto cp)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException {

        if (utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new) == null) {
            throw new UserNotFoundException();
        }

        // Il plusAdding avviene direttamente da un carrello, quindi si presume che il prodotto sia già nel carrello,
        // ma un controllino non fa male
        if (carrelloProdottoRepository.existsByCarrelloAndProdotto(cp.getCarrello(), cp.getProdotto())) {
            Prodotto prod = prodottoRepository.findById(cp.getProdotto().getId()).orElseThrow(ProductNotFoundException::new);
            int disponibilitaProd = prodottoRepository.findDisponibilitaById(prod.getId());

            if (cp.getQuantita() + 1 > disponibilitaProd) {
                throw new InvalidQuantityException("Impossibile aggiungere al carrello: il prodotto non è disponibile per la quantità desiderata");
            }

            cp.setQuantita(cp.getQuantita() + 1);
            carrelloProdottoRepository.save(cp);
        }
    }

    @Transactional
    public void rimuoviDalCarrello(int idUtente, CarrelloProdotto cp)
            throws UserNotFoundException, InvalidOperationException {

        // Recupera l'utente dal database
        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupera il carrello dell'utente
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        // Verifica che il carrello del CarrelloProdotto corrisponda al carrello dell'utente
        if (!cp.getCarrello().equals(carrello)) {
            throw new InvalidOperationException("Operazione non valida: il carrello non corrisponde");
        }

        carrelloProdottoRepository.delete(cp);
    }

    @Transactional
    public void minusRemoving(int idUtente, CarrelloProdotto cp)
            throws UserNotFoundException, InvalidOperationException {

        // Recupera l'utente dal database
        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupera il carrello dell'utente
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        // Verifica che il carrello del CarrelloProdotto corrisponda al carrello dell'utente
        if (!cp.getCarrello().equals(carrello)) {
            throw new InvalidOperationException("Operazione non valida: il carrello non corrisponde");
        }

        // Se la quantità è maggiore di 1, decrementa la quantità
        if (cp.getQuantita() > 1) {
            cp.setQuantita(cp.getQuantita() - 1);
            carrelloProdottoRepository.save(cp);
        } else {
            carrelloProdottoRepository.delete(cp);
        }
    }

    @Transactional
    public void svuotaCarrello(int idUtente)
            throws UserNotFoundException {

        // Recupera l'utente dal database
        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupera il carrello dell'utente
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        carrelloProdottoRepository.deleteAll(carrello.getCarrelloProdottos());
        carrelloRepository.delete(carrello);
    }

    @Transactional
    public void ordina(int idUtente, MetodoDiPagamento metodoDiPagamento, String indirizzoSpedizione)
            throws UserNotFoundException, InvalidOperationException {

        // Recupera l'utente dal database
        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupera il carrello dell'utente
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        // Creazione di un nuovo ordine e una nuova transazione
        Ordine ordine = new Ordine();
        Transazione transazione = new Transazione();

        ordine.setIdCarrello(carrello); // Preso dal db e corrispondente all'utente che ha cliccato
        ordine.setIdUtente(user);
        ordine.setOra(LocalTime.now());
        ordine.setStato("Processamento in corso...");

        transazione.setMetodoDiPagamento(metodoDiPagamento);
        transazione.setIdOrdine(ordine);
        transazione.setOra(LocalTime.now());
        transazione.setData(Instant.now());
        transazione.setImporto(calcolaImporto(carrello));

        Spedizione spedizione = new Spedizione();
        spedizione.setIndirizzoSpedizione(indirizzoSpedizione);

        // Assumo che la spedizione avvenga fra 7 giorni (simulazione)
        spedizione.setDataPrevista(Instant.now().plus(7, ChronoUnit.DAYS));
        spedizione.setStato("In corso...");


        boolean esitoPagamento = processaPagamento(metodoDiPagamento, transazione.getImporto());

        if (esitoPagamento) {
            transazione.setEsito(true);
            ordine.setStato("Pagamento completato");
        } else {
            transazione.setEsito(false);
            ordine.setStato("Pagamento fallito");
        }
    }

    private static BigDecimal calcolaImporto(Carrello carrello) {
        return carrello.getCarrelloProdottos().stream()
                .map(cp -> {
                    BigDecimal prezzo = cp.getProdotto().getPrezzo();
                    BigDecimal quantita = BigDecimal.valueOf(cp.getQuantita());
                    return prezzo.multiply(quantita);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean processaPagamento(MetodoDiPagamento metodoDiPagamento, BigDecimal amount) {
        System.out.println("Pagamento in corso con il metodo selezionato: " + metodoDiPagamento.getSelezione());
        System.out.println("Importo addebitato: " + amount);

        // Simulazione del successo del pagamento con una probabilità dell'80%
        boolean paymentSuccess = RANDOM.nextInt(100) < 80;

        if (paymentSuccess) {
            System.out.println("L'operazione di pagamento ha avuto successo!");
            return true;
        } else {
            System.out.println("Pagamento non andato a buon fine.");
            return false;
        }
    }
}