package org.ecommerce.caramellabeachclub.services;

import jakarta.persistence.LockModeType;
import org.ecommerce.caramellabeachclub.dto.CarrelloProdottoDTO;
import org.ecommerce.caramellabeachclub.entities.ProdottiOrdinati;
import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.*;
import org.ecommerce.caramellabeachclub.resources.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    private OrdineRepository ordineRepository;
    @Autowired
    private TransazioneRepository transazioneRepository;
    @Autowired
    private SpedizioneRepository spedizioneRepository;
    @Autowired
    private ProdottiOrdinatiRepository prodottiOrdinatiRepository;

    @Transactional
    public void aggiungiAlCarrello(String idUtente, int idProdotto, int quantita)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException {

        // Recupera l'utente dal database.
        // Il carrello a cui aggiungere il prodotto te lo trovi tramite l'utente
        // poichè ad ogni utente è associato un solo carrello

        Utente user = utenteRepository.findByEmail(idUtente);
        if (user == null) {
            throw new UserNotFoundException();
        }

        // Recupera il carrello dell'utente o creane uno nuovo se non esiste
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setIdUtente(user.getId());
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
        // di un oggetto che è già presente nel carrello, va fatta considerando la quantità selezionata + quella già
        // presente nel carrello

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
    public void plusAdding(String email, int idProdotto)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException {


        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }

        int idUtente = user.getId();

        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdottoId
                (carrelloRepository.findByIdUtente(idUtente), idProdotto);

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
    public void rimuoviDalCarrello(String email, int prodottoID)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }


        // Recupero il carrello dell'utente
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }

        // Trovo l'elemento del carrello basato sul prodotto
        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdottoId(carrello, prodottoID);
        if (cp == null) {
            throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
        }

        // Rimuovo il prodotto dal carrello
        cp.setQuantita(0); // non si sa mai...
        carrelloProdottoRepository.delete(cp);

        carrelloRepository.save(carrello);
    }


    @Transactional
    public void minusRemoving(String email, int idProdotto)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }
        int idUtente = user.getId();


        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdottoId
                (carrelloRepository.findByIdUtente(idUtente), idProdotto);

        if (!cp.getCarrello().equals(carrello)) {
            throw new InvalidOperationException("Operazione non valida: il carrello non corrisponde");
        }

        // Se la quantità è maggiore di 1, decrementa la quantità
        // sennò lo togli direttamente
        if (cp.getQuantita() > 1) {
            cp.setQuantita(cp.getQuantita() - 1);
            carrelloProdottoRepository.save(cp);
        } else {
            carrelloProdottoRepository.delete(cp);
        }
    }

    @Transactional
    public void svuotaCarrello(String email) throws UserNotFoundException {
        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }

        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        carrelloProdottoRepository.deleteAllByCarrello(carrello);

        carrelloRepository.save(carrello);
    }


    //@Transactional(isolation = Isolation.SERIALIZABLE)
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public void ordina(String email, int metodoDiPagamento, String indirizzoSpedizione)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }
        int idUtente = user.getId();

        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        Set<CarrelloProdotto> prodottiUser = carrelloProdottoRepository.findByCarrelloId(carrello.getIdCarrello());

        if (prodottiUser.isEmpty()) {
            System.out.println("Carrello contenuto: " + carrello.getCarrelloProdottos());
            throw new InvalidOperationException("Il carrello è vuoto. Aggiungi prodotti prima di procedere all'ordine.");
        }

        // Verifico la disponibilità dei prodotti prima di procedere
        for (CarrelloProdotto cp : prodottiUser) {
            Prodotto prodotto = prodottoRepository.findById(cp.getProdottoId())
                    .orElseThrow(() -> new InvalidOperationException("Prodotto non trovato"));

            // Verifico se la quantità disponibile è sufficiente
            if (prodotto.getDisp() < cp.getQuantita()) {
                throw new InvalidOperationException("La quantità del prodotto '" + prodotto.getNome() + "' non è sufficiente per completare l'ordine.");
            }

            // Decremento temporaneamente la quantità disponibile
            prodotto.setDisp(prodotto.getDisp() - cp.getQuantita());
            prodottoRepository.save(prodotto);
        }

        // Creo di un nuovo ordine e una nuova transazione
        Ordine ordine = new Ordine();
        Transazione transazione = new Transazione();

        MetodoDiPagamento met = metodoDiPagamentoRepository.findById(metodoDiPagamento);
        if (met==null){
            throw new InvalidOperationException("Metodo di pagamento non trovato");
        }

        ordine.setIdCarrello(carrello.getIdCarrello()); // Preso dal db e corrispondente all'utente che ha cliccato
        ordine.setIdUtente(user.getId());
        ordine.setOra(LocalTime.now());
        ordine.setData(LocalDateTime.now());
        ordine.setStato("Processamento in corso...");
        ordineRepository.save(ordine);

        transazione.setMetodoDiPagamento(met);
        transazione.setIdOrdine(ordine.getId());
        transazione.setOra(LocalTime.now());
        transazione.setData(Instant.now());
        transazione.setImporto(calcolaImporto(prodottiUser));

        Spedizione spedizione = new Spedizione();
        spedizione.setIdOrdine(ordine.getId());
        spedizione.setIndirizzoSpedizione(indirizzoSpedizione);
        spedizione.setDataPrevista(Instant.now().plus(7, ChronoUnit.DAYS)); // Simulazione spedizione dopo 7 giorni
        spedizione.setStato("In corso...");

        boolean esitoPagamento = processaPagamento(met, transazione.getImporto());

        if (esitoPagamento) {
            transazione.setEsito(true);
            ordine.setStato("Pagamento completato");
            svuotaCarrello(email);

            // Salvo i prodotti ordinati
            for (CarrelloProdotto cp : prodottiUser) {
                ProdottiOrdinati po = new ProdottiOrdinati();
                po.setIdProdotto(cp.getProdottoId());
                po.setIdUtente(user.getId());
                po.setIdOrdine(ordine.getId());
                po.setQuantita(cp.getQuantita());
                prodottiOrdinatiRepository.save(po);
            }

        } else {
            transazione.setEsito(false);
            ordine.setStato("Pagamento fallito");

            // Ripristino la quantità dei prodotti poiché il pagamento è fallito
            for (CarrelloProdotto cp : prodottiUser) {
                Prodotto prodotto = prodottoRepository.findById(cp.getProdottoId())
                        .orElseThrow(() -> new InvalidOperationException("Prodotto non trovato"));

                prodotto.setDisp(prodotto.getDisp() + cp.getQuantita());
                prodottoRepository.save(prodotto);
            }

            throw new InvalidOperationException("Il pagamento è fallito. Riprovare.");
        }

        // Salvo le transazioni e la spedizione
        transazioneRepository.save(transazione);
        spedizioneRepository.save(spedizione);
        ordineRepository.save(ordine);
    }


    private static BigDecimal calcolaImporto(Set<CarrelloProdotto> prodottiUser) {
        BigDecimal totale = BigDecimal.ZERO;

        System.out.println("Calcolo dell'importo totale per il carrello...");

        // Controlla se il carrello ha prodotti
        if (prodottiUser == null || prodottiUser.isEmpty()) {
            System.out.println("Errore: il carrello è vuoto o non contiene prodotti.");
            return totale; // che mi da zero
        }

        for (CarrelloProdotto cp : prodottiUser) {
            Prodotto prodotto = cp.getProdotto();
            BigDecimal prezzo = prodotto.getPrezzo();
            int quantita = cp.getQuantita();

            // Debug
            System.out.println("Prodotto: " + prodotto.getNome());
            System.out.println("Prezzo: " + prezzo);
            System.out.println("Quantità: " + quantita);

            // Controllo validità del prezzo e della quantità
            if (prezzo == null || prezzo.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Errore: prezzo del prodotto " + prodotto.getNome() + " non valido.");
                throw new IllegalArgumentException("Prezzo del prodotto " + prodotto.getNome() + " non valido.");
            }
            if (quantita <= 0) {
                System.out.println("Errore: quantità del prodotto " + prodotto.getNome() + " non valida.");
                throw new IllegalArgumentException("Quantità del prodotto " + prodotto.getNome() + " non valida.");
            }

            // Calcolo del costo per il prodotto e aggiunta al totale
            BigDecimal costoProdotto = prezzo.multiply(BigDecimal.valueOf(quantita));
            System.out.println("Costo per il prodotto: " + costoProdotto);
            totale = totale.add(costoProdotto);
        }

        System.out.println("Totale calcolato: " + totale);
        return totale;
    }

    // Sti qua, alla fin fine, sono tutti metodini di simulazione
    private boolean processaPagamento(MetodoDiPagamento metodoDiPagamento, BigDecimal amount) {

        if (metodoDiPagamento == null) {
            System.out.println("Errore: nessun metodo di pagamento selezionato.");
            return false;
        }

        // Controllo sull'importo
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Errore: importo non valido. Valore importo: " + amount);
            return false;
        }

        System.out.println("Pagamento in corso con il metodo selezionato: " + metodoDiPagamento.getSelezione());
        System.out.println("Importo da pagare: " + amount);

        boolean pagamentoRiuscito = RANDOM.nextInt(100) < 80;

        if (pagamentoRiuscito) {
            System.out.println("Pagamento effettuato con successo!");
            return true;
        } else {
            System.out.println("Pagamento fallito.");
            return false;
        }
    }

    @Transactional
    public List<CarrelloProdottoDTO> getCartItemsByEmail(String email) throws UserNotFoundException {

        Utente user = utenteRepository.findByEmail(email);
        if (user==null){
            throw new UserNotFoundException();
        }

        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        Set<CarrelloProdotto> prodottiUser = carrelloProdottoRepository.findByCarrelloId(carrello.getIdCarrello());

        System.out.println("Carrello contenuto: " + carrello.getCarrelloProdottos());
        System.out.println("Numero di prodotti nel carrello: " + carrello.getCarrelloProdottos().size());

        List<CarrelloProdottoDTO> cartItems = new ArrayList<>();

        for (CarrelloProdotto cp : prodottiUser) {
            System.out.println("Prodotto nel carrello: " + cp.getProdottoId());
            Prodotto prodotto = cp.getProdotto();

            CarrelloProdottoDTO dto = new CarrelloProdottoDTO();
            dto.setIdProdotto(prodotto.getId());
            dto.setNomeProdotto(prodotto.getNome());
            dto.setPrezzo(prodotto.getPrezzo());
            dto.setImageUrl(prodotto.getImmagineUrl());
            dto.setQuantita(cp.getQuantita());

            cartItems.add(dto);
        }

        return cartItems;
    }


}