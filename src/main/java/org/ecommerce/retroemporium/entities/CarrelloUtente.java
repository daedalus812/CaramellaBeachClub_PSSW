package org.ecommerce.retroemporium.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carrello_utente")
public class CarrelloUtente {
    @EmbeddedId
    private CarrelloUtenteId id;

    @MapsId("carrelloId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrello_id", nullable = false, referencedColumnName = "id_carrello")
    private Carrello carrello;

    @MapsId("utenteId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

}