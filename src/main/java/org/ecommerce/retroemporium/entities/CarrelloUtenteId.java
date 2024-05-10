package org.ecommerce.retroemporium.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CarrelloUtenteId implements java.io.Serializable {
    private static final long serialVersionUID = -7365082502322038429L;
    @Column(name = "carrello_id", nullable = false)
    private Integer carrelloId;

    @Column(name = "utente_id", nullable = false)
    private Integer utenteId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CarrelloUtenteId entity = (CarrelloUtenteId) o;
        return Objects.equals(this.carrelloId, entity.carrelloId) &&
                Objects.equals(this.utenteId, entity.utenteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carrelloId, utenteId);
    }

}