package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Carrello;
import org.ecommerce.caramellabeachclub.entities.CarrelloProdotto;
import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrelloProdottoRepository extends JpaRepository<CarrelloProdotto, Integer> {

    boolean existsByCarrelloAndProdotto(Carrello carrello, Prodotto prodotto);
    CarrelloProdotto findByCarrelloAndProdotto(Carrello carrello, Prodotto prod);
}
