package org.ecommerce.azemporium.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CarrelloProdottoId implements java.io.Serializable {
    private static final long serialVersionUID = 1592444027578541388L;
    @Column(name = "carrello_id", nullable = false)
    private Integer carrelloId;

    @Column(name = "prodotto_id", nullable = false)
    private Integer prodottoId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CarrelloProdottoId entity = (CarrelloProdottoId) o;
        return Objects.equals(this.carrelloId, entity.carrelloId) &&
                Objects.equals(this.prodottoId, entity.prodottoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carrelloId, prodottoId);
    }

}