package org.ecommerce.azemporium.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "carrello", uniqueConstraints = {
        @UniqueConstraint(name = "id_carrello", columnNames = {"id_carrello"}),
        @UniqueConstraint(name = "id_utente", columnNames = {"id_utente"})
})
public class Carrello {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrello", nullable = false)
    private Integer idCarrello;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente idUtente;

    @OneToMany(mappedBy = "carrello")
    private Set<CarrelloProdotto> carrelloProdottos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idCarrello")
    private Set<Ordine> ordines = new LinkedHashSet<>();

}