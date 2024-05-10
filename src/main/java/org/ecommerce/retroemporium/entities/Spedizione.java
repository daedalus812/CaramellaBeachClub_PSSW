package org.ecommerce.retroemporium.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "spedizione", uniqueConstraints = {
        @UniqueConstraint(name = "id_ordine", columnNames = {"id_ordine"})
})
public class Spedizione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_spedizione", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ordine", nullable = false)
    private Ordine idOrdine;

    @Column(name = "indirizzo_spedizione", nullable = false, length = 50)
    private String indirizzoSpedizione;

    @Column(name = "data_prevista", nullable = false)
    private Instant dataPrevista;

    @Column(name = "stato", nullable = false, length = 50)
    private String stato;

}