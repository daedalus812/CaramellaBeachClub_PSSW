package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Recensione;
import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Integer> {

    // Trova tutte le recensioni per un prodotto
    List<Recensione> findByIdProdotto(Prodotto prodotto);

    // Trova tutte le recensioni di un utente
    List<Recensione> findByUtente(Utente utente);

    List<Recensione> findAllByUtente(Utente utente);
}
