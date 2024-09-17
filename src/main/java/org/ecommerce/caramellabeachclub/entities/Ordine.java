package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "ordine")
public class Ordine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ordine", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente utente;

    @Getter
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_carrello", nullable = false)
    private Carrello idCarrello;

    @Column(name = "data", nullable = false)
    private Instant data;

    @Column(name = "ora", nullable = false)
    private LocalTime ora;

    @Column(name = "stato", nullable = false, length = Integer.MAX_VALUE)
    private String stato;

    @OneToOne(mappedBy = "idOrdine")
    private Reso resos;

    @OneToOne(mappedBy = "ordine")
    private Spedizione spedizione;

    @OneToOne(mappedBy = "ordine")
    private Transazione transaziones;

    public void setIdCarrello(Carrello carrello) {
        this.idCarrello = idCarrello;
    }

    public void setIdUtente(Utente user) {
        this.utente = user;
    }

}
