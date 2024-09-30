package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.ProdottiOrdinati;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdottiOrdinatiRepository extends JpaRepository<ProdottiOrdinati, Integer> {

    ProdottiOrdinati findProdottiOrdinatiByIdProdotto(int idProdotto);

}