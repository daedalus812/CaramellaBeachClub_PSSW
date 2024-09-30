package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "ordine")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ordine", nullable = false)
    private Integer id;

    @Column(name = "id_utente", nullable = false)
    private int idUtente;

    @Column(name = "id_carrello", nullable = false)
    private int idCarrello;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

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
}
