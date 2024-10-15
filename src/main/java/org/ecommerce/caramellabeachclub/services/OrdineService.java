package org.ecommerce.caramellabeachclub.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.ecommerce.caramellabeachclub.dto.OrdineDTO;
import org.ecommerce.caramellabeachclub.dto.ProdottoOrdinatoDTO;
import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.*;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidOperationException;
import org.ecommerce.caramellabeachclub.resources.exceptions.ProductNotFoundException;
import org.ecommerce.caramellabeachclub.resources.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class OrdineService {

    private static final Random RANDOM = new Random();

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private ResoRepository resoRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private ProdottiOrdinatiRepository prodottiOrdinatiRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * Annullo un ordine esistente e processo il rimborso.
     * L'operazione è atomica e utilizza lock pessimisti per garantire la consistenza dei dati.
     */

    @Transactional
    public void annullaOrdine(Utente u, Ordine o, String motivo) {
        // Lock sull'utente
        Utente utente = utenteRepository.findById(u.getId())
                .orElseThrow(UserNotFoundException::new);
        entityManager.lock(utente, LockModeType.PESSIMISTIC_WRITE);

        // Lock sull'ordine
        Ordine ordine = ordineRepository.findById(o.getId())
                .orElseThrow(InvalidOperationException::new);
        entityManager.lock(ordine, LockModeType.PESSIMISTIC_WRITE);

        if (ordine.getIdUtente()!=(utente.getId())) {
            throw new InvalidOperationException("L'ordine non appartiene all'utente.");
        }

        // Lock sulla spedizione
        Spedizione sped = ordine.getSpedizione();
        if (sped != null) {
            entityManager.lock(sped, LockModeType.PESSIMISTIC_WRITE);
            sped.setStato("Ordine annullato!");
            // Se necessario, salvo la spedizione
            // spedizioneRepository.save(sped);
        }

        ordine.setStato("Annullato. Motivo: " + motivo);
        ordineRepository.save(ordine);

        // Processo il rimborso
        processaRimborso(ordine);
    }

    private void processaRimborso(Ordine ordine) {
        BigDecimal importoRimborso = calcolaImportoRimborso(ordine);
        simulaRimborso(importoRimborso);
    }

    private BigDecimal calcolaImportoRimborso(Ordine ordine) {
        // Lock sulla transazione
        Transazione transazione = ordine.getTransaziones();
        if (transazione != null) {
            entityManager.lock(transazione, LockModeType.PESSIMISTIC_WRITE);
            return transazione.getImporto();
        } else {
            throw new InvalidOperationException("Transazione non trovata per l'ordine.");
        }
    }

    private void simulaRimborso(BigDecimal importo) {
        // Logica di esempio per simulare un rimborso
        System.out.println("Rimborso in corso. Importo: " + importo);

        // Simulazione di una probabilità di successo del 90%
        boolean rimborsoSuccesso = RANDOM.nextInt(100) < 90;

        if (rimborsoSuccesso) {
            System.out.println("Rimborso completato con successo!");
        } else {
            System.out.println("Rimborso fallito.");
            throw new InvalidOperationException("Il rimborso è fallito.");
        }
    }

    /**
     * Effettuo un reso per un ordine esistente e processo il rimborso.
     * L'operazione è atomica e utilizza lock pessimisti per garantire la consistenza dei dati.
     */

    @Transactional
    public void effettuaReso(Utente u, Ordine o, String motivo) {
        // Lock sull'utente
        Utente utente = utenteRepository.findById(u.getId())
                .orElseThrow(UserNotFoundException::new);
        entityManager.lock(utente, LockModeType.PESSIMISTIC_WRITE);

        // Lock sull'ordine
        Ordine ordine = ordineRepository.findById(o.getId())
                .orElseThrow(InvalidOperationException::new);
        entityManager.lock(ordine, LockModeType.PESSIMISTIC_WRITE);

        if (ordine.getIdUtente()!=(utente.getId())) {
            throw new InvalidOperationException("Operazione non valida: l'utente non è associato a questo ordine");
        }

        // Verifico se l'ordine è già stato reso
        if ("Reso Completato, rimborso effettuato.".equals(ordine.getStato())) {
            throw new InvalidOperationException("Questo ordine è già stato reso");
        }

        // Creazione e lock del reso
        Reso reso = new Reso();
        reso.setIdOrdine(ordine);
        reso.setMotivo(motivo);
        reso.setStatoReso("In attesa di elaborazione");

        resoRepository.save(reso);

        ordine.setStato("Reso");
        ordineRepository.save(ordine);

        // Processa il rimborso
        processaRimborso(ordine);
    }

    /**
     * Annullo un reso in corso per un ordine esistente.
     * L'operazione è atomica e utilizza lock pessimisti per garantire la consistenza dei dati.
     */

    @Transactional
    public void annullaReso(Utente u, Ordine o, Reso r) {
        // Lock sull'utente
        Utente utente = utenteRepository.findById(u.getId())
                .orElseThrow(UserNotFoundException::new);
        entityManager.lock(utente, LockModeType.PESSIMISTIC_WRITE);

        // Lock sull'ordine
        Ordine ordine = ordineRepository.findById(o.getId())
                .orElseThrow(InvalidOperationException::new);
        entityManager.lock(ordine, LockModeType.PESSIMISTIC_WRITE);

        if (ordine.getIdUtente()!=(utente.getId())) {
            throw new InvalidOperationException("Operazione non valida: l'utente non è associato a questo ordine");
        }

        // Lock sul reso
        Reso reso = resoRepository.findById(r.getId())
                .orElseThrow(InvalidOperationException::new);
        entityManager.lock(reso, LockModeType.PESSIMISTIC_WRITE);

        if ("Reso Completato, rimborso effettuato.".equals(ordine.getStato())) {
            throw new InvalidOperationException("Questo ordine è già stato reso");
        }

        reso.setStatoReso("Annullato");
        ordine.setStato("Completato");

        resoRepository.save(reso);
        ordineRepository.save(ordine);
    }

    /**
     * Recupero gli ordini di un utente specifico basandosi sull'email.
     * Operazione di sola lettura.
     */

    @Transactional(readOnly = true)
    public List<OrdineDTO> getOrdiniByEmail(String email) throws UserNotFoundException {
        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }

        // Recupero gli ordini dell'utente
        List<Ordine> ordini = ordineRepository.findByIdUtente(user.getId());

        List<OrdineDTO> ordiniDTO = new ArrayList<>();
        for (Ordine ordine : ordini) {
            OrdineDTO dto = new OrdineDTO();
            dto.setIdOrdine(ordine.getId());
            dto.setData(ordine.getData());
            dto.setOra(ordine.getOra());
            dto.setStato(ordine.getStato());

            // Recupero i prodotti ordinati per questo ordine
            List<ProdottiOrdinati> prodottiOrdinati = prodottiOrdinatiRepository.findProdottiOrdinatiByIdOrdine(ordine.getId());

            List<ProdottoOrdinatoDTO> prodottiDTO = new ArrayList<>();
            for (ProdottiOrdinati po : prodottiOrdinati) {

                // Recupero il prodotto utilizzando idProdotto
                Prodotto prodotto = prodottoRepository.findById(po.getIdProdotto())
                        .orElseThrow(ProductNotFoundException::new);

                ProdottoOrdinatoDTO prodottoDTO = new ProdottoOrdinatoDTO();
                prodottoDTO.setIdProdotto(prodotto.getId());
                prodottoDTO.setNome(prodotto.getNome());
                prodottoDTO.setImmagineUrl(prodotto.getImmagineUrl());
                prodottoDTO.setPrezzo(prodotto.getPrezzo());
                prodottoDTO.setQuantita(po.getQuantita());

                prodottiDTO.add(prodottoDTO);
            }
            dto.setProdotti(prodottiDTO);

            ordiniDTO.add(dto);
        }

        return ordiniDTO;
    }
}