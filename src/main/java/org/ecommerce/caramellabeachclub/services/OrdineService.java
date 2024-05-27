package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.Ordine;
import org.ecommerce.caramellabeachclub.entities.Reso;
import org.ecommerce.caramellabeachclub.entities.Transazione;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.repositories.*;
import org.ecommerce.caramellabeachclub.resources.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class OrdineService {

    private static final Random RANDOM = new Random();

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private ResoRepository resoRepository;

    public void annullaOrdine(Utente utente, Ordine ordine, String motivo){
        if (!(ordine.getUtente().equals(utente))){
            throw new InvalidOperationException("Impossibile procedere: l'utente non è associato a questo ordine");
        }

        ordine.setStato("Annullato. Motivo: "+motivo);
        ordineRepository.save(ordine);

        // Si dà per scontato che effettuato l'ordine, il pagamento venga processato all'istante
        // non permettendoti di continuare in caso di esito negativo. Pertanto l'importo è stato certamente
        // pagato e non vi è alcuna possibilità che avvenga un rimborso di importi non pagati.

        // Ho esplicitato questa logica in quanto, ad esempio, Amazon ci mette un po' a prelevare i soldi,
        // infatti quando si effettua un annullamento di un ordine, appare la dicitura:
        // "Se è l'importo è stato già prelevato, riceverete il rimborso entro 1-2 giorni"
        // Ragion per cui, su Amazon, c'è la possibilità che un ordine venga annullato senza che
        // l'importo sia stato effettivamente pagato

        processaRimborso(ordine);

    }

    private boolean processaRimborso(Ordine ordine) {
        BigDecimal importoRimborso = calcolaImportoRimborso(ordine);
        return simulaRimborso(importoRimborso);

    }

    private BigDecimal calcolaImportoRimborso(Ordine ordine) {
        Transazione transazione = ordine.getTransaziones();
        return transazione.getImporto();
    }

    private boolean simulaRimborso(BigDecimal importo) {
        // Logica di esempio per simulare un rimborso
        System.out.println("Rimborso in corso. Importo: " + importo);

        // Simuliamo una probabilità di successo del 90%
        boolean rimborsoSuccesso = RANDOM.nextInt(100) < 90;

        if (rimborsoSuccesso) {
            System.out.println("Rimborso completato con successo!");
            return true;
        } else {
            System.out.println("Rimborso fallito.");
            return false;
        }
    }

    public void effettuaReso(Utente utente, Ordine ordine, String motivo) {
        if (!ordine.getUtente().equals(utente)) {
            throw new InvalidOperationException("Operazione non valida: l'utente non è associato a questo ordine");
        }

        // Verifica se l'ordine è già stato reso
        if (ordine.getStato().equals("Reso")) {
            throw new InvalidOperationException("Questo ordine è già stato reso in precedenza");
        }

        // Creazione dell'oggetto Reso
        Reso reso = new Reso();
        reso.setIdOrdine(ordine);
        reso.setMotivo(motivo);
        reso.setStatoReso("In attesa di elaborazione");

        // Salvataggio del reso nel repository
        resoRepository.save(reso);

        // Aggiornamento dello stato dell'ordine
        ordine.setStato("Reso");
        ordineRepository.save(ordine);

        processaRimborso(ordine);
    }

}