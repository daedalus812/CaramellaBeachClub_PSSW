package org.ecommerce.caramellabeachclub.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.ecommerce.caramellabeachclub.dto.CarrelloProdottoDTO;
import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.*;
import org.ecommerce.caramellabeachclub.resources.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    @Autowired
    private EntityManager entityManager;

    private static final Random RANDOM = new Random();

    @Transactional
    public void aggiungiAlCarrello(String idUtente, int idProdotto, int quantita)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException {

        // Recupero l'utente dal database.
        Utente user = utenteRepository.findByEmail(idUtente);
        if (user == null) {
            throw new UserNotFoundException();
        }

        // Recupero o creo il carrello dell'utente
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setIdUtente(user.getId());
            carrello = carrelloRepository.save(carrello);
        } else {
            // Lock del carrello per evitare accessi concorrenti
            entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);
        }

        // Non locko il prodotto. Lo locko solo quando eseguo un ordine.
        Prodotto prod = prodottoRepository.findById(idProdotto).orElseThrow(ProductNotFoundException::new);
        //entityManager.lock(prod, LockModeType.PESSIMISTIC_WRITE);

        // Verifico la disponibilità del prodotto
        int disponibilitaProd = prod.getDisp();

        if (quantita > disponibilitaProd) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: " +
                    "il prodotto non è disponibile per la quantità desiderata");
        }

        // Controllo se il prodotto è già presente nel carrello
        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdotto(carrello, prod);
        if (cp != null) {
            // Lock dell'elemento del carrello
            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);

            // Aggiorno la quantità
            int nuovaQuantita = cp.getQuantita() + quantita;
            if (nuovaQuantita <= prod.getDisp()) {
                cp.setQuantita(nuovaQuantita);
                carrelloProdottoRepository.save(cp);
            } else {
                throw new InvalidQuantityException("Quantità totale superiore alla disponibilità del prodotto");
            }
        } else {
            // Aggiungo il prodotto al carrello
            CarrelloProdotto aggiunta = new CarrelloProdotto();
            aggiunta.setCarrello(carrello);
            aggiunta.setProdotto(prod);
            aggiunta.setProdottoId(prod.getId());
            aggiunta.setQuantita(quantita);
            aggiunta.setCarrelloId(carrello.getIdCarrello());

            carrelloProdottoRepository.save(aggiunta);
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

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByIdUtente(idUtente);
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdottoId(carrello, idProdotto);
        if (cp == null) {
            throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
        }
        entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);


        Prodotto prod = prodottoRepository.findById(cp.getProdotto().getId()).orElseThrow(ProductNotFoundException::new);
        //entityManager.lock(prod, LockModeType.PESSIMISTIC_WRITE);

        int disponibilitaProd = prod.getDisp();

        if (cp.getQuantita() + 1 > disponibilitaProd) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: " +
                    "il prodotto non è disponibile per la quantità desiderata");
        }
        cp.setQuantita(cp.getQuantita() + 1);
        carrelloProdottoRepository.save(cp);
    }

    @Transactional
    public void rimuoviDalCarrello(String email, int prodottoID)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdottoId(carrello, prodottoID);
        if (cp == null) {
            throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
        }
        entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);

        // Rimuovo il prodotto dal carrello
        carrelloProdottoRepository.delete(cp);
    }

    @Transactional
    public void minusRemoving(String email, int idProdotto)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }

        int idUtente = user.getId();

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByIdUtente(idUtente);
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        CarrelloProdotto cp = carrelloProdottoRepository.findByCarrelloAndProdottoId(carrello, idProdotto);
        if (cp == null) {
            throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
        }
        entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);

        if (!cp.getCarrello().equals(carrello)) {
            throw new InvalidOperationException("Operazione non valida: il carrello non corrisponde");
        }

        // Decremento o rimuovo il prodotto dal carrello
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

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko tutti gli elementi del carrello
        Set<CarrelloProdotto> cartProducts = carrelloProdottoRepository.findByCarrelloId(carrello.getIdCarrello());
        for (CarrelloProdotto cp : cartProducts) {
            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);
        }

        // Svuoto il carrello
        carrelloProdottoRepository.deleteAllByCarrello(carrello);
    }

    @Transactional
    public void ordina(String email, int metodoDiPagamento, String indirizzoSpedizione)
            throws UserNotFoundException, InvalidOperationException {

        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }
        int idUtente = user.getId();

        // Recupero e locko il carrello
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko gli elementi del carrello
        Set<CarrelloProdotto> prodottiUser = carrelloProdottoRepository.findByCarrelloId(carrello.getIdCarrello());
        if (prodottiUser.isEmpty()) {
            throw new InvalidOperationException("Il carrello è vuoto. Aggiungi prodotti prima di procedere all'ordine.");
        }

        // Ordino gli elementi per evitare deadlock
        List<CarrelloProdotto> prodottiUserList = new ArrayList<>(prodottiUser);
        prodottiUserList.sort(Comparator.comparingInt(CarrelloProdotto::getProdottoId));

        // Verifico la disponibilità e locko i prodotti
        for (CarrelloProdotto cp : prodottiUserList) {
            // Lock dell'elemento del carrello
            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);

            Prodotto prodotto = prodottoRepository.findById(cp.getProdottoId())
                    .orElseThrow(() -> new InvalidOperationException("Prodotto non trovato"));
            // Lock del prodotto
            entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);

            // Verifico la disponibilità
            if (prodotto.getDisp() < cp.getQuantita()) {
                throw new InvalidOperationException("La quantità del prodotto '" + prodotto.getNome() + "' non è sufficiente per completare l'ordine.");
            }

            // Decremento la disponibilità del prodotto
            prodotto.setDisp(prodotto.getDisp() - cp.getQuantita());
            prodottoRepository.save(prodotto);
        }

        // Creazione dell'ordine e della transazione
        Ordine ordine = new Ordine();
        Transazione transazione = new Transazione();

        MetodoDiPagamento met = metodoDiPagamentoRepository.findById(metodoDiPagamento);
        if (met == null) {
            throw new InvalidOperationException("Metodo di pagamento non trovato");
        }

        ordine.setIdCarrello(carrello.getIdCarrello());
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
        spedizione.setDataPrevista(Instant.now().plus(7, ChronoUnit.DAYS));
        spedizione.setStato("In corso...");

        boolean esitoPagamento = processaPagamento(met, transazione.getImporto());

        if (esitoPagamento) {
            transazione.setEsito(true);
            ordine.setStato("Pagamento completato");
            svuotaCarrello(email);

            // Salvo i prodotti ordinati
            for (CarrelloProdotto cp : prodottiUserList) {
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

            // Ripristino la quantità dei prodotti
            for (CarrelloProdotto cp : prodottiUserList) {
                Prodotto prodotto = prodottoRepository.findById(cp.getProdottoId())
                        .orElseThrow(() -> new InvalidOperationException("Prodotto non trovato"));
                // Lock del prodotto
                entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);

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

        if (prodottiUser == null || prodottiUser.isEmpty()) {
            return totale;
        }

        for (CarrelloProdotto cp : prodottiUser) {
            Prodotto prodotto = cp.getProdotto();
            BigDecimal prezzo = prodotto.getPrezzo();
            int quantita = cp.getQuantita();

            if (prezzo == null || prezzo.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Prezzo del prodotto " + prodotto.getNome() + " non valido.");
            }
            if (quantita <= 0) {
                throw new IllegalArgumentException("Quantità del prodotto " + prodotto.getNome() + " non valida.");
            }

            BigDecimal costoProdotto = prezzo.multiply(BigDecimal.valueOf(quantita));
            totale = totale.add(costoProdotto);
        }

        return totale;
    }

    private boolean processaPagamento(MetodoDiPagamento metodoDiPagamento, BigDecimal amount) {

        if (metodoDiPagamento == null) {
            System.out.println("Errore: nessun metodo di pagamento selezionato.");
            return false;
        }

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

    @Transactional(readOnly = true)
    public List<CarrelloProdottoDTO> getCartItemsByEmail(String email) throws UserNotFoundException {

        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }

        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }

        Set<CarrelloProdotto> prodottiUser = carrelloProdottoRepository.findByCarrelloId(carrello.getIdCarrello());

        List<CarrelloProdottoDTO> cartItems = new ArrayList<>();

        for (CarrelloProdotto cp : prodottiUser) {
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