//CHECK DONE, OK!
package org.ecommerce.caramellabeachclub.services;

import jakarta.persistence.LockModeType;
import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.*;
import org.ecommerce.caramellabeachclub.resources.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    private ProdottoRepository prodottoRepository;

    private static final Random RANDOM = new Random();
    @Autowired
    private MetodoDiPagamentoRepository metodoDiPagamentoRepository;

    @Transactional
    public void aggiungiAlCarrello(int idUtente, int idProdotto, int quantita)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException {

        // Recupera l'utente dal database
        // il carrello a cui aggiungere il prodotto te lo trovi tramite l'utente
        // poichè ad ogni utente è associato un solo carrello

        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupera il carrello dell'utente o creane uno nuovo se non esiste
        Carrello carrello = carrelloRepository.findByUtenteId(user.getId());
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setUtente(user);
            carrello = carrelloRepository.save(carrello);
        }

        // La variabile prod serve affinché l'oggetto che il client mi passa venga prelevato
        // dal database, e che quindi esista e sia valido
        Prodotto prod = prodottoRepository.findById(idProdotto).orElseThrow(ProductNotFoundException::new);

        // Mi prendo la disponibilità dell'oggetto dal database, grazie alla metodo(query) di ProdottoRepository
        int disponibilitaProd = prodottoRepository.findDisponibilitaById(prod.getId());

        // Verifico che la quantità desiderata sia disponibile (secondo la quantità che il database mi dà)
        if (quantita > disponibilitaProd) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: " +
                    "il prodotto non è disponibile per la quantità desiderata");
        }

        // Ipotizziamo che l'aggiunta si trovi già all'interno del carrello
        // Ciò che faremo è sommare la quantità selezionata a quella già presente nel carrello
        // La disponibilità di un oggetto x (visibile ad altri utenti) non verrà ridotta solo perché si è aggiunto
        // al carrello: va ordinato affinché la disponibilità si riduca. Pertanto il controllo sulla disponibilità
        // di un oggetto che è già presente nel carrello, va fatta considerando la quantità selezionata + quella già presente
        // nel carrello


        // Mi prendo i prodotti già esistenti nel carrello e verifico che non si trovi già ciò che voglio aggiunere
        // In tal caso aggiorno la quantità nel carrello di quel prodotto con quella desiderata

        CarrelloProdotto aggiunta = new CarrelloProdotto();
        aggiunta.setProdotto(prod);

        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdotto(carrello, prod);
            if (cp!=null) {
                // Se il prodotto non è null, vuol dire che è già presente, quindi aggiorno la quantità
                int nuovaQuantita = cp.getQuantita() + quantita;
                if (nuovaQuantita <= prod.getDisp()) {
                    cp.setQuantita(nuovaQuantita); // Aggiorno la quantità totale nel carrello
                    carrelloProdottoRepository.save(cp);
                } else {
                    throw new InvalidQuantityException("Quantità totale superiore alla disponibilità del prodotto");
                }
            } else {
                aggiunta.setCarrello(carrello);
                aggiunta.setProdotto(prod);
                aggiunta.setProdottoId(prod.getId());
                aggiunta.setQuantita(quantita);
                aggiunta.setCarrelloId(carrello.getIdCarrello());

                carrelloProdottoRepository.save(aggiunta);
                carrelloRepository.save(carrello);
            }

    }

    @Transactional
    public void plusAdding(int idUtente, int idProdotto)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException {

        if (utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new) == null) {
            throw new UserNotFoundException();
        }

        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdottoId
                (carrelloRepository.findByUtenteId(idUtente), idProdotto);
        // Il plusAdding avviene direttamente da un carrello, quindi si presume che il prodotto sia già nel carrello,
        // ma un controllino non fa male
        if (carrelloProdottoRepository.existsByCarrelloAndProdotto(cp.getCarrello(), cp.getProdotto())) {
            Prodotto prod = prodottoRepository.findById(cp.getProdotto().getId()).orElseThrow(ProductNotFoundException::new);
            int disponibilitaProd = prodottoRepository.findDisponibilitaById(prod.getId());

            if (cp.getQuantita() + 1 > disponibilitaProd) {
                throw new InvalidQuantityException("Impossibile aggiungere al carrello: " +
                        "il prodotto non è disponibile per la quantità desiderata");
            }

            cp.setQuantita(cp.getQuantita() + 1);
            carrelloProdottoRepository.save(cp);
        } else {
            throw new InvalidQuantityException("Impossibile aumentare la quantità desiderata");
        }
    }

    @Transactional
    public void rimuoviDalCarrello(int idUtente, int prodottoID)
            throws UserNotFoundException, InvalidOperationException {

        // Recupero l'utente dal database
        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupero il carrello dell'utente
        Carrello carrello = carrelloRepository.findByUtenteId(user.getId());
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }

        // Trova l'elemento del carrello basato sul prodotto
        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdottoId(carrello, prodottoID);
        if (cp == null) {
            throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
        }

        // Rimuovi il prodotto dal carrello
        cp.setQuantita(0);
        carrelloProdottoRepository.delete(cp); // Elimina direttamente dal repository

        // Salva le modifiche al carrello
        carrelloRepository.save(carrello);
    }


    @Transactional
    public void minusRemoving(int idUtente, int idProdotto)
            throws UserNotFoundException, InvalidOperationException {

        // Recupera l'utente dal database
        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupera il carrello dell'utente
        Carrello carrello = carrelloRepository.findByUtenteId(user.getId());

        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdottoId
                (carrelloRepository.findByUtenteId(idUtente), idProdotto);


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
    public void svuotaCarrello(int idUtente) throws UserNotFoundException {
        // Recupero l'utente dal database
        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupero il carrello dell'utente
        Carrello carrello = carrelloRepository.findByUtenteId(user.getId());

        // Rimuovo solo i prodotti dal carrello di questo utente
        carrelloProdottoRepository.deleteAllByCarrello(carrello);

        // Aggiorno il carrello (opzionale, dipende da cosa serve nel tuo contesto)
        carrelloRepository.save(carrello);
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void ordina(int idUtente, int metodoDiPagamento, String indirizzoSpedizione)
            throws UserNotFoundException, InvalidOperationException {

        // Recupero l'utente dal database
        Utente user = utenteRepository.findById(idUtente).orElseThrow(UserNotFoundException::new);

        // Recupero il carrello dell'utente
        Carrello carrello = carrelloRepository.findByUtenteId(user.getId());
        Set<CarrelloProdotto> prodottiUser = carrelloProdottoRepository.findByCarrelloId(carrello.getIdCarrello());

        if (prodottiUser.isEmpty()) {
            System.out.println("Carrello contenuto: " + carrello.getCarrelloProdottos());
            throw new InvalidOperationException("Il carrello è vuoto. Aggiungi prodotti prima di procedere all'ordine.");
        }

        // Creazione di un nuovo ordine e una nuova transazione
        Ordine ordine = new Ordine();
        Transazione transazione = new Transazione();

        MetodoDiPagamento met = metodoDiPagamentoRepository.findById(metodoDiPagamento);


        ordine.setIdCarrello(carrello); // Preso dal db e corrispondente all'utente che ha cliccato
        ordine.setIdUtente(user);
        ordine.setOra(LocalTime.now());
        ordine.setStato("Processamento in corso...");

        transazione.setMetodoDiPagamento(met);
        transazione.setIdOrdine(ordine);
        transazione.setOra(LocalTime.now());
        transazione.setData(Instant.now());
        transazione.setImporto(calcolaImporto(carrello));

        Spedizione spedizione = new Spedizione();
        spedizione.setIndirizzoSpedizione(indirizzoSpedizione);

        // Assumo che la spedizione avvenga fra 7 giorni (simulazione)
        spedizione.setDataPrevista(Instant.now().plus(7, ChronoUnit.DAYS));
        spedizione.setStato("In corso...");


        boolean esitoPagamento = processaPagamento(met, transazione.getImporto());

        if (esitoPagamento) {
            transazione.setEsito(true);
            ordine.setStato("Pagamento completato");
            svuotaCarrello(idUtente);
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