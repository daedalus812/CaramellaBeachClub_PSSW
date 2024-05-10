package org.ecommerce.retroemporium.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "transazione")
public class Transazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transazione", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ordine", nullable = false)
    private Ordine idOrdine;

    @Column(name = "data", nullable = false)
    private Instant data;

    @Column(name = "ora", nullable = false)
    private LocalTime ora;

    @Column(name = "importo", nullable = false, precision = 50)
    private BigDecimal importo;

}