package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
