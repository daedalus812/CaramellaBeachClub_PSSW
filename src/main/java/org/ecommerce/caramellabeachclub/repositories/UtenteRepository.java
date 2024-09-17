package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtenteRepository extends JpaRepository<Utente,Integer> {
    public Utente findByEmail(String email);


}
