package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Carrello;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrelloRepository extends JpaRepository<Carrello, Integer> {

    Carrello findByIdUtente(int idUtente);
    @Query("SELECT c FROM Carrello c LEFT JOIN FETCH c.carrelloProdottos WHERE c.idUtente = :idUtente")
    Carrello findByIdUtenteWithProducts(@Param("idUtente") int idUtente);


}