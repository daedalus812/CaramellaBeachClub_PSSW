package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Data
@Getter
@Setter
@Entity
@Table(name = "recensione")
public class Recensione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recensione", nullable = false)
    private Integer id;

    @JoinColumn(name = "id_prodotto", nullable = false)
    private int idProdotto;

    @JoinColumn(name = "id_utente", nullable = false)
    private int idUtente;

    @Column(name = "valutazione", nullable = false, length = 50)
    private String valutazione;

    @Column(name = "commento", length = 100)
    private String commento;

    @Column(name = "data", nullable = false)
    private Instant data;
}