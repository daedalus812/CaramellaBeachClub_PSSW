package org.ecommerce.caramellabeachclub.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "carrello_prodotto")
@IdClass(CarrelloProdottoId.class)
public class CarrelloProdotto {

    @Id
    @Column(name = "carrello_id")
    private Integer carrelloId;

    @Id
    @Column(name = "prodotto_id")
    private Integer prodottoId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    @JoinColumn(name = "carrello_id", insertable = false, updatable = false)
    private Carrello carrello;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prodotto_id", insertable = false, updatable = false)
    @JsonIgnore
    private Prodotto prodotto;

    @Column(name = "quantita")
    private Integer quantita;

    public String getId() {
        return carrelloId + "-" + prodottoId;
    }
}