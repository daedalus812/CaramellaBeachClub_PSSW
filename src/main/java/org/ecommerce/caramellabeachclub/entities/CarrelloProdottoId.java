package org.ecommerce.caramellabeachclub.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class CarrelloProdottoId implements Serializable {

    private Integer carrelloId;
    private Integer prodottoId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrelloProdottoId that = (CarrelloProdottoId) o;
        return Objects.equals(carrelloId, that.carrelloId) && Objects.equals(prodottoId, that.prodottoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carrelloId, prodottoId);
    }
}
