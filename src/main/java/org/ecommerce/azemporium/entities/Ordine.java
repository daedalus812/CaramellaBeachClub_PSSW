package org.ecommerce.azemporium.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "ordine")
public class Ordine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ordine", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente idUtente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_carrello", nullable = false, referencedColumnName = "id_carrello")
    private Carrello idCarrello;

    @Column(name = "data", nullable = false)
    private Instant data;

    @Column(name = "ora", nullable = false)
    private LocalTime ora;

    @Column(name = "stato", nullable = false, length = Integer.MAX_VALUE)
    private String stato;

    @OneToOne(mappedBy = "idOrdine")
    private Reso resos;

    @OneToOne(mappedBy = "idOrdine")
    private Spedizione spedizione;

    @OneToOne(mappedBy = "idOrdine")
    private Transazione transaziones;

    @OneToOne(mappedBy = "idUtente")
    private Utente utente;

}