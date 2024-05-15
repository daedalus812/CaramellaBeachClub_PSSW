package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Integer> {
}
