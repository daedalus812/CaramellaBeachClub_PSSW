package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Categoria;
import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto,Integer> {

    // Lista prodotti mostrati per categoria selezionata
    List<Prodotto> findAllByCategoria(Categoria categoria);

    Prodotto findById(int id);

    @Query("SELECT p.disp FROM Prodotto p WHERE p.id = :id")
    Integer findDisponibilitaById(@Param("id") Integer id);


}
