package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Getter
@Setter
@Entity
@Table(name = "utente")
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utente", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utente", nullable = false)
    private Ordine ordine;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "cognome", nullable = false, length = 50)
    private String cognome;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @OneToOne(mappedBy = "idUtente")
    private Carrello carrello;

    @OneToMany(mappedBy = "idUtente")
    private Set<Ordine> ordines = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUtente")
    private Set<Recensione> recensiones = new LinkedHashSet<>();

}