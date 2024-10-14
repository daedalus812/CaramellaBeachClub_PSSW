package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.dto.OrdineDTO;
import org.ecommerce.caramellabeachclub.dto.ProdottoOrdinatoDTO;
import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.*;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidOperationException;
import org.ecommerce.caramellabeachclub.resources.exceptions.ProductNotFoundException;
import org.ecommerce.caramellabeachclub.resources.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void annullaOrdine(Utente u, Ordine o, String motivo) {
        Utente utente = utenteRepository.findById(u.getId()).orElseThrow(UserNotFoundException::new);
        Ordine ordine = ordineRepository.findById(o.getId()).orElseThrow(InvalidOperationException::new);

        if (!(ordine.getIdUtente() ==(utente.getId()))) {
            throw new InvalidOperationException();
        }

        Spedizione sped = ordine.getSpedizione();
        sped.setStato("Ordine annullato!");

        ordine.setStato("Annullato. Motivo: " + motivo);
        ordineRepository.save(ordine);

        // Si dà per scontato che effettuato l'ordine, il pagamento venga processato all'istante
        // non permettendoti di continuare in caso di esito negativo. Pertanto l'importo è stato certamente
        // pagato e non vi è alcuna possibilità che avvenga un rimborso di importi non pagati.

        // Ho esplicitato questa logica in quanto, ad esempio, Amazon ci mette un po' a prelevare i soldi,
        // infatti quando si effettua un annullamento di un ordine, appare la dicitura:
        // "Se è l'importo è stato già prelevato, riceverete il rimborso entro 1-2 giorni"
        // Ragion per cui, su Amazon, c'è la possibilità che un ordine venga annullato senza che
        // l'importo sia stato effettivamente pagato.

        processaRimborso(ordine);
    }

    private void processaRimborso(Ordine ordine) {
        BigDecimal importoRimborso = calcolaImportoRimborso(ordine);
        simulaRimborso(importoRimborso);
    }

    private BigDecimal calcolaImportoRimborso(Ordine ordine) {
        Transazione transazione = ordine.getTransaziones();
        return transazione.getImporto();
    }

    private void simulaRimborso(BigDecimal importo) {
        // Logica di esempio per simulare un rimborso
        System.out.println("Rimborso in corso. Importo: " + importo);

        // Simuliamo una probabilità di successo del 90%
        boolean rimborsoSuccesso = RANDOM.nextInt(100) < 90;

        if (rimborsoSuccesso) {
            System.out.println("Rimborso completato con successo!");
        } else {
            System.out.println("Rimborso fallito.");
        }
    }

    // Comunque potrei anche considerare il reso come un'azione asincrona,
    // simulando un tempo di elaborazione. Potrebbe essere gestito via un task schedulato in futuro (Se Dio vuole)
    public void effettuaReso(Utente u, Ordine o, String motivo) {
        Utente utente = utenteRepository.findById(u.getId()).orElseThrow(UserNotFoundException::new);
        Ordine ordine = ordineRepository.findById(o.getId()).orElseThrow(InvalidOperationException::new);

        if (!(ordine.getIdUtente() ==(utente.getId()))) {
            throw new InvalidOperationException("Operazione non valida: l'utente non è associato a questo ordine");
        }

        // Verifico se l'ordine è già stato reso
        if (ordine.getStato().equals("Reso Completato, rimborso effettuato.")) {
            throw new InvalidOperationException("Questo ordine è già stato reso");
        }

        Reso reso = new Reso();
        reso.setIdOrdine(ordine);
        reso.setMotivo(motivo);
        reso.setStatoReso("In attesa di elaborazione");

        resoRepository.save(reso);

        ordine.setStato("Reso");
        ordineRepository.save(ordine);

        processaRimborso(ordine);
    }

    public void annullaReso(Utente u, Ordine o, Reso r) {
        Utente utente = utenteRepository.findById(u.getId()).orElseThrow(UserNotFoundException::new);
        Ordine ordine = ordineRepository.findById(o.getId()).orElseThrow(InvalidOperationException::new);

        if (!(ordine.getIdUtente() ==(utente.getId()))) {
            throw new InvalidOperationException("Operazione non valida: l'utente non è associato a questo ordine");
        }

        Reso reso = resoRepository.findById(r.getId()).orElseThrow(InvalidOperationException::new);

        if (ordine.getStato().equals("Reso Completato, rimborso effettuato.")) {
            throw new InvalidOperationException("Questo ordine è già stato reso");
        }

        reso.setStatoReso("Annullato");
        ordine.setStato("Completato");
        resoRepository.save(reso);
    }

    public List<OrdineDTO> getOrdiniByEmail(String email) throws UserNotFoundException {
        Utente user = utenteRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }

        // Recupera gli ordini dell'utente
        List<Ordine> ordini = ordineRepository.findByIdUtente(user.getId());

        List<OrdineDTO> ordiniDTO = new ArrayList<>();
        for (Ordine ordine : ordini) {
            OrdineDTO dto = new OrdineDTO();
            dto.setIdOrdine(ordine.getId());
            dto.setData(ordine.getData());
            dto.setOra(ordine.getOra());
            dto.setStato(ordine.getStato());


            // Recupera i prodotti ordinati per questo ordine
            List<ProdottiOrdinati> prodottiOrdinati = prodottiOrdinatiRepository.findProdottiOrdinatiByIdOrdine(ordine.getId());

            List<ProdottoOrdinatoDTO> prodottiDTO = new ArrayList<>();
            for (ProdottiOrdinati po : prodottiOrdinati) {
                // Recupera il prodotto utilizzando idProdotto
                Prodotto prodotto = prodottoRepository.findById(po.getIdProdotto())
                        .orElseThrow(ProductNotFoundException::new);

                ProdottoOrdinatoDTO prodottoDTO = new ProdottoOrdinatoDTO();
                prodottoDTO.setIdProdotto(prodotto.getId());
                prodottoDTO.setNome(prodotto.getNome());
                prodottoDTO.setImmagineUrl(prodotto.getImmagineUrl());
                prodottoDTO.setPrezzo(prodotto.getPrezzo());
                prodottoDTO.setQuantita(po.getQuantita()); // Assicurati che ProdottiOrdinati abbia il campo quantita

                prodottiDTO.add(prodottoDTO);
            }
            dto.setProdotti(prodottiDTO);

            ordiniDTO.add(dto);
        }

        return ordiniDTO;
    }

}