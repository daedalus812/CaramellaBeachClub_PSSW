package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione,Integer> {
}
