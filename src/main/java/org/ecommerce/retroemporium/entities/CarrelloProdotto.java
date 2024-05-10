package org.ecommerce.retroemporium.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carrello_prodotto")
public class CarrelloProdotto {
    @EmbeddedId
    private CarrelloProdottoId id;

    @MapsId("carrelloId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrello_id", nullable = false, referencedColumnName = "id_carrello")
    private Carrello carrello;

    @MapsId("prodottoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prodotto_id", nullable = false)
    private Prodotto prodotto;

    @Column(name = "quantita")
    private Integer quantita;

}