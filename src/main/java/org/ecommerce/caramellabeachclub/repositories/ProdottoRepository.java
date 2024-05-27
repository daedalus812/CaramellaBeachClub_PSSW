package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Categoria;
import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto,Integer> {

    // Lista prodotti mostrati per categoria selezionata
    List<Prodotto> findAllByCategoria(Categoria categoria);
}
