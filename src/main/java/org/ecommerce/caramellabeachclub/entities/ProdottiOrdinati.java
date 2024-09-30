package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "prodotti_ordinati")
public class ProdottiOrdinati {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_prodotto", nullable = false)
    private Integer idProdotto;

    @Column(name = "id_ordine", nullable = false)
    private Integer idOrdine;

    @Column(name = "id_utente", nullable = false)
    private Integer idUtente;

}