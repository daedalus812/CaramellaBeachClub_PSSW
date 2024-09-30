package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Carrello;
import org.ecommerce.caramellabeachclub.entities.CarrelloProdotto;
import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface CarrelloProdottoRepository extends JpaRepository<CarrelloProdotto, Integer> {

    boolean existsByCarrelloAndProdotto(Carrello carrello, Prodotto prodotto);
    CarrelloProdotto findByCarrelloAndProdotto(Carrello carrello, Prodotto prod);
    CarrelloProdotto findByCarrelloAndProdottoId(Carrello carrello, int prod);

    @Query("SELECT cp FROM CarrelloProdotto cp WHERE cp.carrello.idCarrello = :idCarrello")
    Set<CarrelloProdotto> findByCarrelloId(@Param("idCarrello") int idCarrello);

    void deleteAllByCarrello(Carrello carrello);
}