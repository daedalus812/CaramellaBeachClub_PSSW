package org.ecommerce.caramellabeachclub.repositories;

import org.ecommerce.caramellabeachclub.entities.MetodoDiPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoDiPagamentoRepository extends JpaRepository<MetodoDiPagamento, Integer> {

    // Puoi aggiungere metodi personalizzati qui, se necessario.
    // Ad esempio, se vuoi cercare metodi di pagamento per selezione:

    MetodoDiPagamento findById(int id);
}
