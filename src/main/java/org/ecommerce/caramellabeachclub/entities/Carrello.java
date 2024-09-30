package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
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

    @JoinColumn(name = "id_utente", nullable = false)
    private int idUtente;

    @OneToMany(mappedBy = "carrello", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CarrelloProdotto> carrelloProdottos = new LinkedHashSet<>();
}