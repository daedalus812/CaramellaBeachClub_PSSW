package org.ecommerce.azemporium.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "recensione")
public class Recensione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recensione", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prodotto", nullable = false)
    private Prodotto idProdotto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente idUtente;

    @Column(name = "valutazione", nullable = false, length = 50)
    private String valutazione;

    @Column(name = "commento", length = 100)
    private String commento;

    @Column(name = "data", nullable = false)
    private Instant data;

}