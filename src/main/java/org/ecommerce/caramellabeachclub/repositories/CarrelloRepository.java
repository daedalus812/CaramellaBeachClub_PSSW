package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Carrello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrelloRepository extends JpaRepository<Carrello, Integer> {

    //trova il carrello tramite l'ID utente
    Carrello findByUtenteId(int utente);
    Carrello getCarrelloByUtenteId(int utente);

    @Query("SELECT c FROM Carrello c LEFT JOIN FETCH c.carrelloProdottos WHERE c.utente.id = :idUtente")
    Carrello findByUtenteIdWithProducts(@Param("idUtente") int idUtente);

}
