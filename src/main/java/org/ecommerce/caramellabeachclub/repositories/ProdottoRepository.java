package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto,Integer> {
}
